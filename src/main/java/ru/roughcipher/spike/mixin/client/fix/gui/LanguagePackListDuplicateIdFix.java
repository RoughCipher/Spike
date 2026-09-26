package ru.roughcipher.spike.mixin.client.fix.gui;

import net.minecraft.client.gui.options.components.LanguagePackListComponent;
import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.roughcipher.spike.client.LanguagePackSelectionState;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(LanguagePackListComponent.class)
public abstract class LanguagePackListDuplicateIdFix {

	@Inject(method = "onMouseClick", at = @At("HEAD"))
	private void spike$rememberClickedPack(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY, CallbackInfo ci) {
		List<?> buttons;
		try {
			Field buttonsField = LanguagePackListComponent.class.getDeclaredField("buttons");
			buttonsField.setAccessible(true);
			buttons = (List<?>) buttonsField.get(this);
		} catch (ReflectiveOperationException e) {
			return;
		}
		if (buttons == null) {
			return;
		}
		for (int i = 0; i < buttons.size(); i++) {
			if (relativeMouseX < 0 || relativeMouseX > width
				|| relativeMouseY < 3 + i * 35
				|| relativeMouseY > 3 + i * 35 + 32) {
				continue;
			}
			Object button = buttons.get(i);
			try {
				Field packField = button.getClass().getDeclaredField("languagePack");
				packField.setAccessible(true);
				Language pack = (Language) packField.get(button);
				LanguagePackSelectionState.setSelected(pack);
			} catch (ReflectiveOperationException ignored) {
			}
			break;
		}
	}
}
