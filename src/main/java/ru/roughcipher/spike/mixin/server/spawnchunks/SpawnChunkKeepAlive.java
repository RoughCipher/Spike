package ru.roughcipher.spike.mixin.server.spawnchunks;

import net.minecraft.core.world.Dimension;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import ru.roughcipher.spike.config.SpikeConfig;

@Mixin(ChunkProviderServer.class)
public abstract class SpawnChunkKeepAlive {

	@Shadow
	@Final
	private WorldServer world;

	@ModifyConstant(
		method = "dropChunk",
		constant = @Constant(intValue = 128)
	)
	private int spike$spawnKeepRadius(int original) {
		if (!SpikeConfig.spawnChunks) {
			return 0;
		}
		if (this.world.dimension != Dimension.OVERWORLD) {
			return 0;
		}
		return original;
	}
}
