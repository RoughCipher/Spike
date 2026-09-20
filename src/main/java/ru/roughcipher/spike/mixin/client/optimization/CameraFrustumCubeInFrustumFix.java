package ru.roughcipher.spike.mixin.client.optimization;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.culling.CameraFrustum;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CameraFrustum.class)
public abstract class CameraFrustumCubeInFrustumFix {

	@WrapMethod(method = "cubeInFrustum")
	private boolean spike$cubeInFrustumRespectOption(AABBdc aabb, float partialTick, Operation<Boolean> original) {
		return ((CameraFrustum) (Object) this).isVisible(aabb, partialTick);
	}
}
