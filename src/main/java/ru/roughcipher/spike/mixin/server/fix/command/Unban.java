package ru.roughcipher.spike.mixin.server.fix.command;

import net.minecraft.server.net.PlayerList;
import net.minecraft.server.net.command.commands.CommandUnban;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(CommandUnban.class)
public abstract class Unban {

	@Redirect(
		method = "lambda$register$2",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/net/PlayerList;banPlayer(Ljava/util/UUID;)V"
		)
	)
	private static void spike$pardonInsteadOfBan(PlayerList playerList, UUID uuid) {
		playerList.pardonPlayer(uuid);
	}
}
