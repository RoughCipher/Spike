package ru.roughcipher.spike.mixin.core.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(Entity.class)
public abstract class SharedEntityRandom {

	@Unique
	private static final Random SPIKE$SHARED_RANDOM = new Random();

	@WrapOperation(
		method = "<init>(Lnet/minecraft/core/world/World;)V",
		at = @At(value = "NEW", target = "()Ljava/util/Random;")
	)
	private static Random spike$useSharedRandom(Operation<Random> original) {
		return SPIKE$SHARED_RANDOM;
	}
}
