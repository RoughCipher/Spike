package ru.roughcipher.spike.mixin.core.simulationdistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.world.SpawnerMobs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.roughcipher.spike.config.SpikeConfig;

@Mixin(SpawnerMobs.class)
public abstract class SimulationDistanceSpawning {

	@ModifyExpressionValue(
		method = "performSpawning",
		at = @At(value = "CONSTANT", args = "intValue=8")
	)
	private static int spike$spawnRadiusPositive(int original) {
		return SpikeConfig.simulationDistance;
	}

	@ModifyExpressionValue(
		method = "performSpawning",
		at = @At(value = "CONSTANT", args = "intValue=-8")
	)
	private static int spike$spawnRadiusNegative(int original) {
		return -SpikeConfig.simulationDistance;
	}
}
