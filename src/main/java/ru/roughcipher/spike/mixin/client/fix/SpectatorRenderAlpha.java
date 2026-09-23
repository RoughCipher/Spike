package ru.roughcipher.spike.mixin.client.fix;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MobRenderer.class, remap = false)
public abstract class SpectatorRenderAlpha {

	@Unique
	private static final float SPECTATOR_ALPHA = 0.35f;

	@Unique
	private static boolean spike$isSpectator(Player player) {
		if (player == null) {
			return false;
		}
		Gamemode gm = player.getGamemode();
		return gm == Gamemodes.SPECTATOR;
	}

	@Unique
	private static boolean spike$viewerIsSpectator() {
		Minecraft mc = Minecraft.getMinecraft();
		PlayerLocal local = mc.thePlayer;
		return spike$isSpectator(local);
	}

	@ModifyReturnValue(
		method = "getRenderAlpha(Lnet/minecraft/core/entity/Mob;F)F",
		at = @At("RETURN"),
		require = 0
	)
	private float spike$spectatorAlpha(float original, Mob entity, float partialTick) {
		if (entity instanceof Player player && spike$isSpectator(player) && spike$viewerIsSpectator()) {
			return SPECTATOR_ALPHA;
		}
		return original;
	}
}
