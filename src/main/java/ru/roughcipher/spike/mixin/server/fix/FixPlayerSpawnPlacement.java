package ru.roughcipher.spike.mixin.server.fix;

import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.world.ServerPlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.roughcipher.spike.server.WorldSpawnPlacement;

import java.util.UUID;

@Mixin(PlayerServer.class)
public abstract class FixPlayerSpawnPlacement {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$placeAtWorldSpawn(
		MinecraftServer minecraftserver,
		World world,
		String username,
		UUID uuid,
		ServerPlayerController serverPlayerController,
		CallbackInfo ci
	) {
		WorldSpawnPlacement.moveToWorldSpawn((PlayerServer) (Object) this, world);
	}
}
