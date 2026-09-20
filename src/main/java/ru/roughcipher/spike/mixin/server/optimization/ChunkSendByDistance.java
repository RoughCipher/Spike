package ru.roughcipher.spike.mixin.server.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.world.ServerPlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.SERVER)
@Mixin(PlayerServer.class)
public abstract class ChunkSendByDistance {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$replaceLoadedChunksWithArrayList(
			MinecraftServer minecraftserver,
			World world,
			String username,
			UUID uuid,
			ServerPlayerController serverPlayerController,
			CallbackInfo ci
	) {
		PlayerServer self = (PlayerServer) (Object) this;
		List<ChunkCoordinate> current = self.loadedChunks;
		if (current == null || current instanceof ArrayList) {
			return;
		}
		self.loadedChunks = new ArrayList<>(current);
	}

	@WrapOperation(
		method = "tickSendChunks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;get(I)Ljava/lang/Object;",
			ordinal = 0
		)
	)
	private Object spike$getNearestPendingChunk(List<ChunkCoordinate> list, int index, Operation<Object> original) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		Entity self = (Entity) (Object) this;
		int nearestIdx = spike$indexOfNearest(list, self.x, self.z);
		if (nearestIdx < 0) {
			return list.get(0);
		}
		return list.get(nearestIdx);
	}

	@WrapOperation(
		method = "tickSendChunks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;remove(I)Ljava/lang/Object;",
			ordinal = 0
		)
	)
	private Object spike$removeNearestPendingChunk(List<ChunkCoordinate> list, int index, Operation<Object> original) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		Entity self = (Entity) (Object) this;
		int nearestIdx = spike$indexOfNearest(list, self.x, self.z);
		if (nearestIdx < 0) {
			return list.remove(0);
		}
		return list.remove(nearestIdx);
	}

	@Unique
	private int spike$indexOfNearest(List<ChunkCoordinate> list, double playerX, double playerZ) {
		int playerChunkX = (int) playerX >> 4;
		int playerChunkZ = (int) playerZ >> 4;

		int bestIdx = -1;
		int bestDist = Integer.MAX_VALUE;

		for (int i = 0, size = list.size(); i < size; i++) {
			ChunkCoordinate c = list.get(i);
			if (c == null) {
				continue;
			}
			int dx = c.x - playerChunkX;
			int dz = c.z - playerChunkZ;
			int dist = dx * dx + dz * dz;
			if (dist < bestDist) {
				bestDist = dist;
				bestIdx = i;
				if (dist == 0) {
					break;
				}
			}
		}
		return bestIdx;
	}
}
