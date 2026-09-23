package ru.roughcipher.spike.mixin.server.fix;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.EntityTrackerEntryImpl;
import net.minecraft.server.entity.EntityTrackerImpl;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.roughcipher.spike.server.SpectatorGamemodeRefreshState;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = PlayerServer.class, remap = false)
public abstract class SpectatorGamemodeRefresh {

	@Shadow
	public MinecraftServer mcServer;

	@Unique
	private static boolean spike$isSpectator(Gamemode gm) {
		return gm == Gamemodes.SPECTATOR;
	}

	@Inject(
		method = "setGamemode(Lnet/minecraft/core/player/gamemode/Gamemode;)V",
		at = @At("HEAD")
	)
	private void spike$captureOldGamemode(Gamemode gamemode, CallbackInfo ci) {
		PlayerServer self = (PlayerServer) (Object) this;
		SpectatorGamemodeRefreshState.previous = self.getGamemode();
	}

	@Inject(
		method = "setGamemode(Lnet/minecraft/core/player/gamemode/Gamemode;)V",
		at = @At("RETURN")
	)
	private void spike$refreshTrackingAfterGamemode(Gamemode gamemode, CallbackInfo ci) {
		Gamemode oldMode = SpectatorGamemodeRefreshState.previous;
		SpectatorGamemodeRefreshState.previous = null;

		boolean wasSpectator = spike$isSpectator(oldMode);
		boolean nowSpectator = spike$isSpectator(gamemode);

		PlayerServer self = (PlayerServer) (Object) this;

		if (nowSpectator) {
			self.setNoPhysics(true);
		}

		if (wasSpectator == nowSpectator) {
			return;
		}

		World w = self.world;
		if (w == null || this.mcServer == null) {
			return;
		}
		Dimension dim = w.dimension;
		if (dim == null) {
			return;
		}

		EntityTrackerImpl tracker = this.mcServer.getEntityTracker(dim.id);
		if (tracker == null) {
			return;
		}

		EntityTrackerEntryImpl entry = tracker.trackedEntityHashTable.get(self.id);
		if (entry == null) {
			return;
		}

		if (nowSpectator) {
			List<PlayerServer> toRemove = new ArrayList<>();
			for (PlayerServer viewer : entry.trackedPlayers) {
				if (viewer != self && !spike$isSpectator(viewer.getGamemode())) {
					toRemove.add(viewer);
				}
			}
			for (PlayerServer viewer : toRemove) {
				entry.removeTrackedPlayerSymmetric(viewer);
			}
		} else {
			List<Player> players = w.players;
			if (players != null) {
				entry.updatePlayerEntities(players);
			}
		}
	}
}
