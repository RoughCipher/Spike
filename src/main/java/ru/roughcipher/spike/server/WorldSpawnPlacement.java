package ru.roughcipher.spike.server;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public final class WorldSpawnPlacement {

	private WorldSpawnPlacement() {
	}

	public static void moveToWorldSpawn(Player player, World world) {
		TilePos spawn = world.getSpawnPoint();
		player.moveTo(spawn.x + 0.5, spawn.y + 0.1, spawn.z + 0.5, player.yRot, player.xRot);
		liftOutOfSolid(player, world);
	}

	public static double SpawnY(World world) {
		return world.getSpawnPoint().y + 0.1;
	}

	public static void liftOutOfSolid(Player player, World world) {
		for (int i = 0; i < 256; i++) {
			if (world.getCubes(player, player.bb).isEmpty()) {
				return;
			}
			player.setPos(player.x, player.y + 1.0, player.z);
		}
	}
}
