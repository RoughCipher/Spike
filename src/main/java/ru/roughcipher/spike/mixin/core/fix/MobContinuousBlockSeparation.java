package ru.roughcipher.spike.mixin.core.fix;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.roughcipher.spike.util.ChunkLoadCollisionState;
import ru.roughcipher.spike.util.EntityBlockSeparation;

@Mixin(value = Mob.class, remap = false)
public abstract class MobContinuousBlockSeparation {

	@Shadow
	public abstract boolean isAlive();

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void spike$continuousSeparation(CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;

		if (!this.isAlive() || entity.removed || entity.hasNoPhysics()) {
			return;
		}

		boolean postLoad = ChunkLoadCollisionState.getPostLoadRetries(entity) > 0;
		if (!postLoad && !entity.isInWall()) {
			return;
		}

		if (EntityBlockSeparation.pushOut(entity)) {
			ChunkLoadCollisionState.grantWallImmunity(entity, 10);
		}
	}
}
