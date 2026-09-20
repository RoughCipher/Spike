package ru.roughcipher.spike.mixin.server.optimization;

import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.pos.ChunkPos;
import net.minecraft.core.world.pos.ChunkPosc;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.roughcipher.spike.server.chunk.AsyncChunkGen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkProviderServer.class)
public abstract class AsyncChunkGeneration {

	@Shadow
	public ChunkGenerator chunkGenerator;

	@Final
	@Shadow
	private WorldServer world;

	@Shadow
	@Final
	@Mutable
	private Map<ChunkPos, Chunk> chunkMap;

	@Unique
	private int spike$dimensionId() {
		return this.world != null
				? this.world.getDimension().id
				: 0;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$useConcurrentChunkMap(CallbackInfo ci) {
		this.chunkMap = new ConcurrentHashMap<>(this.chunkMap);
	}

	@WrapOperation(
		method = "prepareChunk",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/generate/chunk/ChunkGenerator;generate(II)Lnet/minecraft/core/world/chunk/Chunk;"
		)
	)
	private Chunk spike$asyncGeneratePrepare(ChunkGenerator generator, int chunkX, int chunkZ, Operation<Chunk> original) {
		Chunk prefetched = AsyncChunkGen.takeIfDone(generator, chunkX, chunkZ);
		if (prefetched != null) {
			return prefetched;
		}
		return AsyncChunkGen.generateSync(generator, chunkX, chunkZ, spike$dimensionId());
	}

	@WrapOperation(
		method = "regenerateChunk",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/generate/chunk/ChunkGenerator;generate(II)Lnet/minecraft/core/world/chunk/Chunk;"
		)
	)
	private Chunk spike$asyncGenerateRegen(ChunkGenerator generator, int chunkX, int chunkZ, Operation<Chunk> original) {
		return AsyncChunkGen.generateSync(generator, chunkX, chunkZ, spike$dimensionId());
	}

	@Inject(method = "prepareChunk", at = @At("RETURN"))
	private void spike$prefetchNeighbors(
			ChunkPosc chunkPos,
			boolean priority,
			CallbackInfoReturnable<Chunk> cir
	) {
		if (this.chunkGenerator == null || cir.getReturnValue() == null) {
			return;
		}
		AsyncChunkGen.prefetchNeighbors(
				this.chunkGenerator,
				chunkPos.x(),
				chunkPos.z(),
				spike$dimensionId()
		);
	}
}
