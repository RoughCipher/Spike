package ru.roughcipher.spike.mixin.client.optimization;

import net.minecraft.client.render.culling.CameraFrustum;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraFrustum.class)
public abstract class CameraFrustumCubeInFrustumFix {

	@Inject(
		method = "cubeInFrustum",
		at = @At("HEAD"),
		cancellable = true
	)
	private void spike$cubeInFrustumRespectOption(AABBdc aabb, float partialTick, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(((CameraFrustum) (Object) this).isVisible(aabb, partialTick));
	}
}
