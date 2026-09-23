package ru.roughcipher.spike.mixin.core.fix;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Entity.class, remap = false)
public abstract class SpectatorMuteSteps {

	@Shadow
	public float fallDistance;

	@Shadow
	protected boolean wasInWater;

	@Shadow
	public abstract boolean checkAndHandleWater(boolean addVelocity);

	@Unique
	private static boolean spike$isSpectatorPlayer(Entity entity) {
		if (!(entity instanceof Player player)) {
			return false;
		}
		Gamemode gm = player.getGamemode();
		return gm == Gamemodes.SPECTATOR;
	}

	@ModifyReturnValue(method = "makeStepSound()Z", at = @At("RETURN"))
	private boolean spike$noSpectatorSteps(boolean original) {
		if (spike$isSpectatorPlayer((Entity) (Object) this)) {
			return false;
		}
		return original;
	}

	@WrapMethod(method = "checkFallDamage(DZ)V")
	private void spike$noSpectatorFallAccumulate(double yd, boolean onGround, Operation<Void> original) {
		if (spike$isSpectatorPlayer((Entity) (Object) this)) {
			this.fallDistance = 0.0f;
			return;
		}
		original.call(yd, onGround);
	}

	@WrapMethod(method = "checkOnWater(Z)V")
	private void spike$noSpectatorWaterFx(boolean addVelocity, Operation<Void> original) {
		if (spike$isSpectatorPlayer((Entity) (Object) this)) {
			this.wasInWater = this.checkAndHandleWater(addVelocity);
			return;
		}
		original.call(addVelocity);
	}
}
