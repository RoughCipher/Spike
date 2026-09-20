package ru.roughcipher.spike.server.chunk;

import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class AsyncChunkGen {

	private static final Logger LOGGER = LoggerFactory.getLogger("spike/chunk");

	private static final int WORKERS = Math.max(1, Math.min(3,
			Runtime.getRuntime().availableProcessors() - 1));

	private static final AtomicLong SEQ = new AtomicLong();

	private static final PriorityBlockingQueue<PrioritizedTask> QUEUE =
			new PriorityBlockingQueue<>(256);

	static {
		for (int i = 0; i < WORKERS; i++) {
			Thread worker = new Thread(() -> {
				while (true) {
					try {
						PrioritizedTask task = QUEUE.take();
						task.runnable.run();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					} catch (Throwable ex) {
						LOGGER.error("Chunk gen worker crashed", ex);
					}
				}
			}, "spike-chunk-gen-" + (i + 1));
			worker.setDaemon(true);
			worker.setPriority(Thread.MIN_PRIORITY);
			worker.start();
		}
		LOGGER.info("AsyncChunkGen started with {} workers", WORKERS);
	}

	private static final ConcurrentHashMap<GenKey, CompletableFuture<Chunk>> PENDING =
			new ConcurrentHashMap<>();

	private AsyncChunkGen() {}

	private static GenKey key(ChunkGenerator generator, int x, int z) {
		return new GenKey(System.identityHashCode(generator), x, z);
	}

	public static int priorityFor(int chunkX, int chunkZ, int dimensionId) {
		MinecraftServer server = MinecraftServer.getInstance();
		if (server == null || server.playerList == null) {
			return Integer.MAX_VALUE / 2;
		}
		List<PlayerServer> players = server.playerList.playerEntities;
		if (players == null || players.isEmpty()) {
			return Integer.MAX_VALUE / 2;
		}
		int best = Integer.MAX_VALUE / 2;
		for (PlayerServer p : players) {
			if (p == null || p.dimension != dimensionId) {
				continue;
			}
			int dx = ((int) p.x >> 4) - chunkX;
			int dz = ((int) p.z >> 4) - chunkZ;
			int d = dx * dx + dz * dz;
			if (d < best) {
				best = d;
				if (d == 0) {
					break;
				}
			}
		}
		return best;
	}

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	private static Chunk generateLocked(@NotNull ChunkGenerator generator, int x, int z) {
		synchronized (generator) {
			Chunk chunk = generator.generate(x, z);
			chunk.fixMissingBlocks();
			return chunk;
		}
	}

	private static void purgeOrphans() {
		if (PENDING.size() < 512) {
			return;
		}
		PENDING.entrySet().removeIf(e -> e.getValue().isDone());
	}

	private static Executor executorFor(int priority) {
		final int p = priority;
		return r -> QUEUE.offer(new PrioritizedTask(p, SEQ.getAndIncrement(), r));
	}

	@NotNull
	public static Chunk generateSync(@NotNull ChunkGenerator generator, int x, int z,
			@Nullable Integer dimensionId) {
		purgeOrphans();
		GenKey k = key(generator, x, z);
		int priority = dimensionId != null
				? priorityFor(x, z, dimensionId)
				: Integer.MAX_VALUE / 4;

		CompletableFuture<Chunk> fut = PENDING.computeIfAbsent(k, ignored ->
				CompletableFuture.supplyAsync(() -> {
					try {
						return generateLocked(generator, x, z);
					} catch (Throwable ex) {
						LOGGER.error("Async terrain gen failed at {}, {} (gen {})",
								x, z, System.identityHashCode(generator), ex);
						throw ex instanceof RuntimeException
								? (RuntimeException) ex
								: new RuntimeException(ex);
					}
				}, executorFor(priority))
		);

		try {
			return fut.join();
		} finally {
			PENDING.remove(k, fut);
		}
	}

	public static void prefetch(ChunkGenerator generator, int x, int z,
			@Nullable Integer dimensionId) {
		if (generator == null) {
			return;
		}
		GenKey k = key(generator, x, z);
		int priority = dimensionId != null
				? priorityFor(x, z, dimensionId)
				: Integer.MAX_VALUE / 3;
		PENDING.computeIfAbsent(k, ignored ->
				CompletableFuture.supplyAsync(() -> {
					try {
						return generateLocked(generator, x, z);
					} catch (Throwable ex) {
						LOGGER.error("Prefetch terrain gen failed at {}, {} (gen {})",
								x, z, System.identityHashCode(generator), ex);
						PENDING.remove(k);
						throw ex instanceof RuntimeException
								? (RuntimeException) ex
								: new RuntimeException(ex);
					}
				}, executorFor(priority))
		);
	}

	public static void prefetchNeighbors(ChunkGenerator generator, int cx, int cz,
			@Nullable Integer dimensionId) {
		if (generator == null) {
			return;
		}
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				if (dx == 0 && dz == 0) {
					continue;
				}
				prefetch(generator, cx + dx, cz + dz, dimensionId);
			}
		}
	}

	@Nullable
	public static Chunk takeIfDone(ChunkGenerator generator, int x, int z) {
		if (generator == null) {
			return null;
		}
		GenKey k = key(generator, x, z);
		CompletableFuture<Chunk> fut = PENDING.get(k);
		if (fut == null || !fut.isDone() || fut.isCompletedExceptionally()) {
			return null;
		}
		try {
			Chunk c = fut.join();
			PENDING.remove(k, fut);
			return c;
		} catch (Throwable ignored) {
			PENDING.remove(k, fut);
			return null;
		}
	}

	private record PrioritizedTask(int priority, long seq, Runnable runnable)
			implements Comparable<PrioritizedTask> {

		@Override
		public int compareTo(PrioritizedTask o) {
			int c = Integer.compare(this.priority, o.priority);
			if (c != 0) {
				return c;
			}
			return Long.compare(this.seq, o.seq);
		}
	}

	private record GenKey(int generatorId, int x, int z) {
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof GenKey other)) {
				return false;
			}
			return generatorId == other.generatorId && x == other.x && z == other.z;
		}

		@Override
		public int hashCode() {
			return Objects.hash(generatorId, x, z);
		}
	}
}
