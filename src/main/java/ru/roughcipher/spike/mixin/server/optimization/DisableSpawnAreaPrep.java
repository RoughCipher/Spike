package ru.roughcipher.spike.mixin.server.optimization;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public abstract class DisableSpawnAreaPrep {

	@ModifyConstant(
		method = "initWorld",
		constant = @Constant(intValue = 196)
	)
	private int spike$zeroSpawnPrepRadius(int original) {
		return 0;
	}
}
