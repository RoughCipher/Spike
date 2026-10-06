package ru.roughcipher.spike.mixin.client.fix.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.chat.GuiElementChatSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiElementChatSuggestions.class)
public class ChatSuggestionsCursorSafe {

	@WrapOperation(
		method = "finishUpdatingSuggestions",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/String;substring(II)Ljava/lang/String;"
		)
	)
	private String spike$safeSuggestionPrefix(String self, int beginIndex, int endIndex, Operation<String> original) {
		int len = self.length();
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		if (endIndex > len) {
			endIndex = len;
		}
		if (beginIndex > endIndex) {
			return "";
		}
		return original.call(self, beginIndex, endIndex);
	}
}
