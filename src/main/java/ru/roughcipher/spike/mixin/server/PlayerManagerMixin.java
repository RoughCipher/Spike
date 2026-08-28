package ru.roughcipher.spike.mixin.server;

import net.minecraft.server.player.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
		)
	)
	private Iterator<?> spike$iterateSnapshot(List<?> list) {
		return new ArrayList<>(list).iterator();
	}
}
