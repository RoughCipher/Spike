package ru.roughcipher.spike.mixin.server.fix.command;

import com.mojang.brigadier.arguments.ArgumentTypeString;
import net.minecraft.server.net.command.commands.CommandBan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandBan.class)
public abstract class Ban {

	@Redirect(
		method = "register",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/brigadier/arguments/ArgumentTypeString;word()Lcom/mojang/brigadier/arguments/ArgumentTypeString;",
			ordinal = 1
		)
	)
	private static ArgumentTypeString spike$ipv6CompatibleIpArg() {
		return ArgumentTypeString.greedyString();
	}
}
