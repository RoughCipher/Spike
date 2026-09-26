package ru.roughcipher.spike.mixin.client.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MobRendererPlayer.class, remap = false)
public abstract class SpectatorPlayerShadow {

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

	@Unique
	private static boolean spike$ghostSpectator(Player player) {
		return spike$isSpectator(player) && spike$viewerIsSpectator();
	}

	@ModifyReturnValue(
		method = "getShadowSize(Lnet/minecraft/core/entity/player/Player;)F",
		at = @At("RETURN"),
		require = 0
	)
	private float spike$noSpectatorShadow(float original, Player entity) {
		if (spike$isSpectator(entity) && !spike$viewerIsSpectator()) {
			return 0.0f;
		}
		return original;
	}

	@WrapOperation(
		method = "renderAdditional(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/player/Player;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/renderer/GLRenderer;setColor4f(FFFF)V"
		)
	)
	private void spike$spectatorCapeColor(
		float r, float g, float b, float a,
		Operation<Void> original,
		TessellatorGeneral tessellator, Player player, float partialTick
	) {
		if (spike$ghostSpectator(player) && a >= 0.99f) {
			original.call(r, g, b, SPECTATOR_ALPHA);
		} else {
			original.call(r, g, b, a);
		}
	}

	@WrapMethod(
		method = "renderAdditional(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/player/Player;F)V"
	)
	private void spike$spectatorCapeWrap(
		TessellatorGeneral tessellator,
		Player player,
		float partialTick,
		Operation<Void> original
	) {
		boolean ghost = spike$ghostSpectator(player);
		if (ghost) {
			GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
			GLRenderer.enableState(State.BLEND);
			GLRenderer.setDepthMask(false);
			GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, SPECTATOR_ALPHA);
		}
		try {
			original.call(tessellator, player, partialTick);
		} finally {
			if (ghost) {
				GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GLRenderer.setDepthMask(true);
			}
		}
	}

	@WrapMethod(
		method = "drawFirstPersonHand(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/player/Player;Z)V"
	)
	private void spike$spectatorHandAlpha(
		TessellatorGeneral tessellator,
		Player player,
		boolean isLeft,
		Operation<Void> original
	) {
		boolean spectator = spike$isSpectator(player);
		if (spectator) {
			GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
			GLRenderer.enableState(State.BLEND);
			GLRenderer.setDepthMask(false);
			GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, SPECTATOR_ALPHA);
		}
		try {
			original.call(tessellator, player, isLeft);
		} finally {
			if (spectator) {
				GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GLRenderer.setDepthMask(true);
			}
		}
	}
}
