package ru.roughcipher.spike.mixin.server.fix.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.net.command.commands.CommandUnban;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(CommandUnban.class)
public abstract class Unban {

	@WrapOperation(
		method = "lambda$register$2",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/net/PlayerList;banPlayer(Ljava/util/UUID;)V"
		)
	)
	private static void spike$pardonInsteadOfBan(PlayerList playerList, UUID uuid, Operation<Void> original) {
		playerList.pardonPlayer(uuid);
	}

	@WrapOperation(
		method = "register",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/brigadier/arguments/ArgumentTypeString;word()Lcom/mojang/brigadier/arguments/ArgumentTypeString;",
			ordinal = 1
		)
	)
	private static ArgumentTypeString spike$ipv6CompatibleIpArg(Operation<ArgumentTypeString> original) {
		return ArgumentTypeString.greedyString();
	}
}
