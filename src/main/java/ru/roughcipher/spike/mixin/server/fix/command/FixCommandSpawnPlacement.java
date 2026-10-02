package ru.roughcipher.spike.mixin.server.fix.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.commands.CommandSpawn;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.roughcipher.spike.server.WorldSpawnPlacement;

@Mixin(CommandSpawn.class)
public abstract class FixCommandSpawnPlacement {

	@WrapOperation(
		method = {
			"lambda$register$0",
			"lambda$register$1"
		},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/net/command/CommandSource;teleportPlayerToPos"
				+ "(Lnet/minecraft/core/entity/player/Player;DDD)V"
		)
	)
	private static void spike$teleportToSpawn(
		CommandSource source,
		Player player,
		double x,
		double y,
		double z,
		Operation<Void> original
	) {
		World world = source.getWorld(0);
		TilePos spawn = world.getSpawnPoint();
		double newX = spawn.x + 0.5;
		double newY = WorldSpawnPlacement.SpawnY(world);
		double newZ = spawn.z + 0.5;
		original.call(source, player, newX, newY, newZ);
		WorldSpawnPlacement.liftOutOfSolid(player, world);
	}
}
