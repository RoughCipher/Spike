package ru.roughcipher.spike.mixin.client.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.terrain.ChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public abstract class LimitNearChunkRebuilds {

	@Unique
	private int spike$nearRebuildsThisFrame;

	@Inject(method = "updateDirtyChunks", at = @At("HEAD"))
	private void spike$resetNearBudget(ICamera camera, CallbackInfoReturnable<Boolean> cir) {
		this.spike$nearRebuildsThisFrame = 0;
	}

	@WrapOperation(
		method = "updateDirtyChunks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/terrain/ChunkRenderer;rebuild(Z)Z",
			ordinal = 0
		)
	)
	private boolean spike$budgetNearRebuild(
			ChunkRenderer chunk,
			boolean force,
			Operation<Boolean> original
	) {
		int budget = budget();
		if (this.spike$nearRebuildsThisFrame >= budget) {
			return false;
		}
		boolean did = original.call(chunk, force);
		if (did) {
			this.spike$nearRebuildsThisFrame++;
		}
		return did;
	}

	@Unique
	private static int budget() {
		int base = GameSettings.CHUNK_REBUILDS_PER_FRAME.value;
		return Math.max(4, base * 2);
	}
}
