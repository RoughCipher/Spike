package ru.roughcipher.spike.mixin.core.fix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.roughcipher.spike.util.ChunkLoadCollisionState;
import ru.roughcipher.spike.util.EntityBlockSeparation;

@Mixin(value = Entity.class, remap = false)
public abstract class EntityChunkLoadCollisionFix {

	@Shadow public World world;
	@Shadow public double x;
	@Shadow public double y;
	@Shadow public double z;
	@Shadow public float bbWidth;
	@Shadow public float bbHeight;
	@Shadow public float heightOffset;
	@Shadow public float ySlideOffset;
	@Shadow @Final public AABBd bb;
	@Shadow public boolean removed;

	@Shadow public abstract boolean hasNoPhysics();

	@WrapMethod(method = "setBounds")
	private void spike$setBoundsSymmetric(Operation<Void> original) {
		double halfW = this.bbWidth * 0.5;
		double minX = this.x - halfW;
		double maxX = minX + this.bbWidth;
		double minZ = this.z - halfW;
		double maxZ = minZ + this.bbWidth;
		double minY = this.y - this.heightOffset + this.ySlideOffset;
		double maxY = minY + this.bbHeight;
		this.bb.setMin(minX, minY, minZ);
		this.bb.setMax(maxX, maxY, maxZ);
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void spike$afterLoad(CompoundTag tag, CallbackInfo ci) {
		ChunkLoadCollisionState.markLoaded((Entity) (Object) this);
	}

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void spike$postLoadSeparation(CallbackInfo ci) {
		Entity self = (Entity) (Object) this;

		ChunkLoadCollisionState.tickWallImmunity(self);

		int retries = ChunkLoadCollisionState.getPostLoadRetries(self);
		if (retries <= 0) {
			return;
		}
		if (this.removed || this.hasNoPhysics()) {
			ChunkLoadCollisionState.clearPostLoad(self);
			return;
		}

		if (EntityBlockSeparation.pushOut(self)) {
			ChunkLoadCollisionState.clearPostLoad(self);
			ChunkLoadCollisionState.grantWallImmunity(self, 20);
		} else {
			ChunkLoadCollisionState.consumePostLoadRetry(self);
		}
	}
}
