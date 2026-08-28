package ru.roughcipher.spike.mixin.server.optimization;

import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChunkProviderServer.class)
public abstract class DisableSpawnChunkKeepAlive {

	@ModifyConstant(
		method = "dropChunk",
		constant = @Constant(intValue = 128)
	)
	private int spike$zeroSpawnKeepRadius(int original) {
		return 0;
	}
}
