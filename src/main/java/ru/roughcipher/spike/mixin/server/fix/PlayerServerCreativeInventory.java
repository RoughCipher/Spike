package ru.roughcipher.spike.mixin.server.fix;

import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerServer.class)
public abstract class PlayerServerCreativeInventory {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$ensureCreativeInventoryOnFirstJoin(CallbackInfo ci) {
		PlayerServer self = (PlayerServer) (Object) this;
		Gamemode mode = self.getGamemode();
		if (mode != null) {
			self.setGamemodeOnLogin(mode);
		}
	}
}
