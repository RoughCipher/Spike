package ru.roughcipher.spike.mixin.server.fix;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class DefaultGamemode {

	@Shadow
	public PropertyManager propertyManager;

	@Shadow
	public Gamemode defaultGamemode;

	@Redirect(
		method = "startServer",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"
		)
	)
	private void spike$suppressGamemodeWarn(Logger logger, String message) {
		if (!message.contains("Unrecognised gamemode")) {
			logger.warn(message);
		}
	}

	@Inject(
		method = "startServer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/data/registry/Registries;<init>()V",
			shift = At.Shift.AFTER
		)
	)
	private void spike$fixDefaultGamemode(CallbackInfoReturnable<Boolean> cir) {
		String value = this.propertyManager.getStringProperty(
			"default-gamemode",
			"minecraft:gamemode/survival"
		);

		Gamemode mode = Registries.GAMEMODES.getItem(value);
		if (mode == null) {
			mode = Gamemodes.SURVIVAL;
			System.err.println("Unrecognised gamemode \"" + value +
				"\" after Registries init! Falling back to survival.");
		}

		this.defaultGamemode = mode;
	}
}
