package ru.roughcipher.spike.mixin.client.optimization;

import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.client.world.WorldClientMP;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.net.packet.PacketChunkBlocksUpdate;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketHandlerClient.class)
public abstract class ChunkBlocksUpdateBatch {

	@Shadow
	private WorldClientMP worldClientMP;

	@Inject(method = "handleChunkBlocksUpdate", at = @At("HEAD"), cancellable = true)
	private void spike$batchChunkBlocksUpdate(PacketChunkBlocksUpdate packetChunkBlocksUpdate, CallbackInfo ci) {
		WorldClientMP world = this.worldClientMP;
		if (world == null || packetChunkBlocksUpdate.size <= 0) {
			ci.cancel();
			return;
		}

		int baseX = packetChunkBlocksUpdate.xChunk << 4;
		int baseZ = packetChunkBlocksUpdate.zChunk << 4;

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;

		for (int i = 0; i < packetChunkBlocksUpdate.size; i++) {
			int coord = packetChunkBlocksUpdate.coordinateArray[i];
			int x = (coord & 15) + baseX;
			int y = (coord >> 8) & 255;
			int z = ((coord >> 4) & 15) + baseZ;
			int type = packetChunkBlocksUpdate.typeArray[i] & 16383;
			int meta = packetChunkBlocksUpdate.metadataArray[i] & 0xFF;

			Block<?> block = Blocks.getBlock(type);
			world.setBlockTypeDataRaw(new TilePos(x, y, z), block, meta);

			if (x < minX) minX = x;
			if (y < minY) minY = y;
			if (z < minZ) minZ = z;
			if (x > maxX) maxX = x;
			if (y > maxY) maxY = y;
			if (z > maxZ) maxZ = z;
		}

		TilePos min = new TilePos(minX, minY, minZ);
		TilePos max = new TilePos(maxX, maxY, maxZ);
		world.removePositionTypesInBounds(min, max);
		world.markBlocksDirty(min, max);
		ci.cancel();
	}
}
