package ru.roughcipher.spike.mixin.core.fix.command;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.packet.PacketCommandManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PacketCommandManager.class)
public class CommandSuggestionsCursorSafe {

	@WrapMethod(method = "getDispatcherSuggestions")
	private static JsonObject spike$safeDispatcherSuggestions(
			CommandDispatcher<CommandSource> dispatcher,
			CommandSource source,
			String text,
			int cursor,
			Operation<JsonObject> original
	) {
		try {
			return original.call(dispatcher, source, text, cursor);
		} catch (IllegalStateException e) {
			return new JsonObject();
		}
	}
}
