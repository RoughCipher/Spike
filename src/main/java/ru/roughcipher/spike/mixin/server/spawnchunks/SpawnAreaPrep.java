package ru.roughcipher.spike.mixin.server.spawnchunks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import ru.roughcipher.spike.config.SpikeConfig;

@Mixin(MinecraftServer.class)
public abstract class SpawnAreaPrep {

	@ModifyConstant(
		method = "initWorld",
		constant = @Constant(intValue = 196)
	)
	private int spike$spawnPrepRadius(int original) {
		return SpikeConfig.spawnChunks ? original : 0;
	}

	@WrapOperation(
		method = "initWorld",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/net/PropertyManager;getBooleanProperty(Ljava/lang/String;Z)Z"
		)
	)
	private boolean spike$skipNonOverworldSpawnPrep(
		PropertyManager instance,
		String key,
		boolean defaultValue,
		Operation<Boolean> original
	) {
		if (SpikeConfig.spawnChunks && ("allow-nether".equals(key) || "allow-drift".equals(key))) {
			return false;
		}
		return original.call(instance, key, defaultValue);
	}
}
