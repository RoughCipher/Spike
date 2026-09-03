package ru.roughcipher.spike.mixin.server.fix.worldgen;

import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.chunk.ChunkLoader;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.pos.ChunkPos;
import net.minecraft.core.world.pos.ChunkPosc;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;

@Mixin(ChunkProviderServer.class)
public abstract class RecursiveChunkGen {

	@Unique
	private final ArrayDeque<ChunkPos> spike$pendingDecorate = new ArrayDeque<>();

	@Unique
	private boolean spike$decoratedThisTick;

	@Shadow
	public boolean chunkLoadOverride;

	@Shadow
	private boolean decorating;

	@Final
	@Shadow
	private WorldServer world;

	@Shadow
	public abstract void populate(ChunkPosc chunkPos);

	@Unique
	private boolean spike$isNether() {
		return this.world != null && this.world.getDimension() == Dimension.NETHER;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$init(
		WorldServer world,
		ChunkLoader chunkLoader,
		ChunkGenerator chunkGenerator,
		CallbackInfo ci
	) {
		this.chunkLoadOverride = true;
	}

	@Redirect(
		method = "provideChunk",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/world/chunk/provider/ChunkProviderServer;decorating:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private boolean spike$allowTerrainDuringDecorate(ChunkProviderServer self) {
		return false;
	}

	@Inject(method = "populate", at = @At("HEAD"), cancellable = true)
	private void spike$limitDecoratePerTick(ChunkPosc chunkPos, CallbackInfo ci) {
		if (!spike$isNether()) {
			return;
		}
		if (this.decorating) {
			return;
		}

		if (this.spike$decoratedThisTick) {
			ChunkPos pos = new ChunkPos(chunkPos.x(), chunkPos.z());
			if (!this.spike$pendingDecorate.contains(pos)) {
				this.spike$pendingDecorate.addLast(pos);
			}
			ci.cancel();
			return;
		}

		this.spike$decoratedThisTick = true;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void spike$processDecorateQueue(CallbackInfoReturnable<Boolean> cir) {
		if (!spike$isNether()) {
			return;
		}

		this.spike$decoratedThisTick = false;

		if (this.decorating || this.spike$pendingDecorate.isEmpty()) {
			return;
		}

		ChunkPos next = this.spike$pendingDecorate.pollFirst();
		if (next != null) {
			this.populate(next);
		}
	}

	@Inject(method = "saveChunks", at = @At("HEAD"))
	private void spike$dropQueueOnSave(
		boolean saveImmediately,
		@Nullable ProgressListener progressListener,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (saveImmediately && spike$isNether()) {
			this.spike$pendingDecorate.clear();
			this.spike$decoratedThisTick = false;
		}
	}
}
