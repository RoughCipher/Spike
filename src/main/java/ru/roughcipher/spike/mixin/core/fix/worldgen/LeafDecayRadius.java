package ru.roughcipher.spike.mixin.core.fix.worldgen;

import net.minecraft.core.block.BlockLogicLeavesBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockLogicLeavesBase.class)
public abstract class LeafDecayRadius {

	@ModifyConstant(
		method = "updateTick",
		constant = @Constant(intValue = 4)
	)
	private int spike$increaseDecayRadius(int original) {
		return 7;
	}
}
