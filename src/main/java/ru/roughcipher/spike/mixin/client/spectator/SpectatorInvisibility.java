package ru.roughcipher.spike.mixin.client.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MobRenderer.class, remap = false)
public abstract class SpectatorInvisibility {

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
	private static boolean spike$hideFromViewer(Entity entity) {
		return entity instanceof Player player
			&& spike$isSpectator(player)
			&& !spike$viewerIsSpectator();
	}

	@WrapMethod(
		method = "render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;DDDFF)V",
		require = 0
	)
	private void spike$skipSpectatorBodyEntity(
		TessellatorGeneral tessellator,
		Entity entity,
		double x, double y, double z,
		float yaw, float partialTick,
		Operation<Void> original
	) {
		if (spike$hideFromViewer(entity)) {
			return;
		}
		original.call(tessellator, entity, x, y, z, yaw, partialTick);
	}

	@WrapMethod(
		method = "render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Mob;DDDFF)V",
		require = 0
	)
	private void spike$skipSpectatorBodyMob(
		TessellatorGeneral tessellator,
		Mob entity,
		double x, double y, double z,
		float yaw, float partialTick,
		Operation<Void> original
	) {
		if (spike$hideFromViewer(entity)) {
			return;
		}
		original.call(tessellator, entity, x, y, z, yaw, partialTick);
	}

	@ModifyReturnValue(
		method = "getShadowSize(Lnet/minecraft/core/entity/Entity;)F",
		at = @At("RETURN"),
		require = 0
	)
	private float spike$noSpectatorShadowEntity(float original, Entity entity) {
		if (spike$hideFromViewer(entity)) {
			return 0.0f;
		}
		return original;
	}

	@ModifyReturnValue(
		method = "getShadowSize(Lnet/minecraft/core/entity/Mob;)F",
		at = @At("RETURN"),
		require = 0
	)
	private float spike$noSpectatorShadowMob(float original, Mob entity) {
		if (spike$hideFromViewer(entity)) {
			return 0.0f;
		}
		return original;
	}
}
