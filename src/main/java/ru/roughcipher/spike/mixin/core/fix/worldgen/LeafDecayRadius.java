package ru.roughcipher.spike.mixin.core.fix.worldgen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Random;

@Mixin(BlockLogicLeavesBase.class)
public abstract class LeafDecayRadius {

	@Unique
	private static final int DECAY_RADIUS = 7;
	@Unique
	private static final int SIZE = DECAY_RADIUS * 2 + 1;
	@Unique
	private static final int SIZE2 = SIZE * SIZE;
	@Unique
	private static final int VISITED_LEN = SIZE * SIZE * SIZE;
	@Unique
	private static final int QUEUE_CAP = VISITED_LEN;

	@Unique
	private static final ThreadLocal<boolean[]> VISITED =
			ThreadLocal.withInitial(() -> new boolean[VISITED_LEN]);

	@Unique
	private static final ThreadLocal<int[]> QUEUE =
			ThreadLocal.withInitial(() -> new int[QUEUE_CAP]);

	@Unique
	private static final ThreadLocal<TilePos> CHECK_POS =
			ThreadLocal.withInitial(TilePos::new);

	@Unique
	private static final int[][] DIRS = {
			{1, 0, 0}, {-1, 0, 0},
			{0, 1, 0}, {0, -1, 0},
			{0, 0, 1}, {0, 0, -1}
	};

	@Shadow
	public static boolean isDecaying(int meta) {
		return false;
	}

	@Shadow
	public static boolean isPermanent(int meta) {
		return false;
	}

	@Shadow
	public static int setDecaying(int meta, boolean decaying) {
		return 0;
	}

	@Shadow
	private void removeLeaves(World world, TilePosc tilePos) {}

	@WrapMethod(method = "updateTick")
	private void spike$optimizedLeafDecay(
			World world,
			TilePosc tilePos,
			Random rand,
			boolean isRandomTick,
			Operation<Void> original
	) {
		if (world.isClientSide) {
			return;
		}

		int meta = world.getBlockData(tilePos);
		if (!isDecaying(meta) || isPermanent(meta)) {
			return;
		}

		int offset = DECAY_RADIUS + 1;
		if (!world.areBlocksLoaded(
				tilePos.add(-offset, -offset, -offset, new TilePos()),
				tilePos.add(offset, offset, offset, new TilePos()))) {
			return;
		}

		if (hasNearbyLogBFS(world, tilePos)) {
			world.setBlockData(tilePos, setDecaying(meta, false));
		} else {
			this.removeLeaves(world, tilePos);
		}
	}

	@Unique
	private boolean hasNearbyLogBFS(World world, TilePosc origin) {
		boolean[] visited = VISITED.get();
		Arrays.fill(visited, false);

		int[] queue = QUEUE.get();
		int head = 0;
		int tail = 0;

		queue[tail++] = pack(0, 0, 0, 0);
		visited[index(0, 0, 0)] = true;

		TilePos check = CHECK_POS.get();
		final int ox = origin.x();
		final int oy = origin.y();
		final int oz = origin.z();

		while (head < tail) {
			int packed = queue[head++];
			int dx = unpackDx(packed);
			int dy = unpackDy(packed);
			int dz = unpackDz(packed);
			int dist = unpackDist(packed);

			check.set(ox + dx, oy + dy, oz + dz);
			BlockLogic logic = world.getBlockType(check).getLogic();
			if (logic instanceof BlockLogicLog) {
				return true;
			}

			if (!(logic instanceof BlockLogicLeavesBase) || dist >= DECAY_RADIUS) {
				continue;
			}

			for (int[] d : DIRS) {
				int ndx = dx + d[0];
				int ndy = dy + d[1];
				int ndz = dz + d[2];

				if (Math.abs(ndx) > DECAY_RADIUS
						|| Math.abs(ndy) > DECAY_RADIUS
						|| Math.abs(ndz) > DECAY_RADIUS) {
					continue;
				}

				int idx = index(ndx, ndy, ndz);
				if (visited[idx]) {
					continue;
				}
				visited[idx] = true;

				check.set(ox + ndx, oy + ndy, oz + ndz);
				BlockLogic nLogic = world.getBlockType(check).getLogic();

				if (nLogic instanceof BlockLogicLog) {
					return true;
				}
				if (nLogic instanceof BlockLogicLeavesBase && tail < QUEUE_CAP) {
					queue[tail++] = pack(ndx, ndy, ndz, dist + 1);
				}
			}
		}

		return false;
	}

	@Unique
	private static int pack(int dx, int dy, int dz, int dist) {
		return (dx + DECAY_RADIUS)
				| ((dy + DECAY_RADIUS) << 5)
				| ((dz + DECAY_RADIUS) << 10)
				| (dist << 15);
	}

	@Unique
	private static int unpackDx(int p) {
		return (p & 31) - DECAY_RADIUS;
	}

	@Unique
	private static int unpackDy(int p) {
		return ((p >> 5) & 31) - DECAY_RADIUS;
	}

	@Unique
	private static int unpackDz(int p) {
		return ((p >> 10) & 31) - DECAY_RADIUS;
	}

	@Unique
	private static int unpackDist(int p) {
		return (p >> 15) & 31;
	}

	@Unique
	private static int index(int dx, int dy, int dz) {
		return (dx + DECAY_RADIUS) * SIZE2
				+ (dy + DECAY_RADIUS) * SIZE
				+ (dz + DECAY_RADIUS);
	}
}
