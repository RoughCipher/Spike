package ru.roughcipher.spike.mixin.core.simulationdistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.roughcipher.spike.config.SpikeConfig;

@Mixin(World.class)
public abstract class SimulationDistanceBlocks {

	@ModifyExpressionValue(
		method = "updateBlocksAndPlayCaveSounds",
		at = @At(value = "CONSTANT", args = "intValue=9")
	)
	private int spike$simulationDistancePositive(int original) {
		return SpikeConfig.simulationDistance;
	}

	@ModifyExpressionValue(
		method = "updateBlocksAndPlayCaveSounds",
		at = @At(value = "CONSTANT", args = "intValue=-9")
	)
	private int spike$simulationDistanceNegative(int original) {
		return -SpikeConfig.simulationDistance;
	}
}
