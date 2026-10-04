package ru.roughcipher.spike.mixin.core.fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.roughcipher.spike.util.ChunkLoadCollisionState;

@Mixin(value = Mob.class, remap = false)
public abstract class MobChunkLoadWallImmunity {

	@WrapOperation(
		method = "baseTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/entity/Mob;isInWall()Z"
		)
	)
	private boolean spike$skipWallDamageWhileFixing(Mob instance, Operation<Boolean> original) {
		if (ChunkLoadCollisionState.hasWallImmunity(instance)) {
			return false;
		}
		return original.call(instance);
	}
}
