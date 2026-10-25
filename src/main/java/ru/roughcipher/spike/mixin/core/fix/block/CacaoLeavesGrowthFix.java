package ru.roughcipher.spike.mixin.core.fix.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesCacao;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: Удалить при обновлении.
@Mixin(BlockLogicLeavesCacao.class)
public abstract class CacaoLeavesGrowthFix {

	@Unique
	private static final int MIN_GROWTH_HARVESTABLE = 2;

	@ModifyConstant(
		method = "canBeansGrow",
		constant = @Constant(intValue = 1, ordinal = 0)
	)
	private int spike$fixLoopUpperBoundX(int original) {
		return 2;
	}

	@ModifyConstant(
		method = "canBeansGrow",
		constant = @Constant(intValue = 1, ordinal = 1)
	)
	private int spike$fixLoopUpperBoundZ(int original) {
		return 2;
	}

	@WrapOperation(
		method = "canBeansGrow",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/World;getBlockType(Lnet/minecraft/core/world/pos/TilePosc;)Lnet/minecraft/core/block/Block;"
		)
	)
	private Block<?> spike$checkAirInsteadOfNull(
		World world,
		TilePosc tilePos,
		Operation<Block<?>> original
	) {
		Block<?> block = original.call(world, tilePos);
		return block == Blocks.AIR ? null : block;
	}

	@Inject(method = "harvest", at = @At("HEAD"), cancellable = true)
	private void spike$blockImmatureHarvest(
		World world,
		TilePosc tilePos,
		Player player,
		CallbackInfoReturnable<Boolean> cir
	) {
		int growthRate = BlockLogicLeavesCacao.getGrowthRate(world.getBlockData(tilePos));
		if (growthRate < MIN_GROWTH_HARVESTABLE) {
			cir.setReturnValue(false);
		}
	}
}
