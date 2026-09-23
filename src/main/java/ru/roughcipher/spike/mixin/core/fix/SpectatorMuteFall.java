package ru.roughcipher.spike.mixin.core.fix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Mob.class, remap = false)
public abstract class SpectatorMuteFall {

	@Unique
	private static boolean spike$isSpectator(Mob mob) {
		if (!(mob instanceof Player player)) {
			return false;
		}
		Gamemode gm = player.getGamemode();
		return gm == Gamemodes.SPECTATOR;
	}

	@WrapMethod(method = "causeFallDamage(F)V")
	private void spike$noSpectatorLandSound(float distance, Operation<Void> original) {
		if (spike$isSpectator((Mob) (Object) this)) {
			return;
		}
		original.call(distance);
	}
}
