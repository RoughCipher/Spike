package ru.roughcipher.spike.mixin.server.fix.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import net.minecraft.server.net.command.commands.CommandBan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandBan.class)
public abstract class Ban {

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
