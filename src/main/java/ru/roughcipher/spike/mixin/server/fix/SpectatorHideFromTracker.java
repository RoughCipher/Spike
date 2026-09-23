package ru.roughcipher.spike.mixin.server.fix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.server.entity.EntityTrackerEntryImpl;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityTrackerEntryImpl.class, remap = false)
public abstract class SpectatorHideFromTracker {

	@Shadow
	public Entity trackedEntity;

	@Shadow
	public abstract void removeTrackedPlayerSymmetric(Player player);

	@Unique
	private static boolean spike$isSpectator(Player player) {
		if (player == null) {
			return false;
		}
		Gamemode gm = player.getGamemode();
		return gm == Gamemodes.SPECTATOR;
	}

	@WrapMethod(method = "updatePlayerEntity(Lnet/minecraft/core/entity/player/Player;)V")
	private void spike$hideSpectatorFromNonSpectators(Player player, Operation<Void> original) {
		Entity tracked = this.trackedEntity;
		if (tracked instanceof Player trackedPlayer
			&& spike$isSpectator(trackedPlayer)
			&& !spike$isSpectator(player)) {
			if (player instanceof PlayerServer) {
				this.removeTrackedPlayerSymmetric(player);
			}
			return;
		}
		original.call(player);
	}
}
