package ru.roughcipher.spike.mixin.client.fix.gui;

import net.minecraft.client.option.GameSettings;
import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.roughcipher.spike.client.LanguagePackSelectionState;

@Mixin(targets = "net.minecraft.client.gui.options.components.LanguagePackListComponent$LanguagePackComponent")
public abstract class LanguagePackComponentDuplicateIdFix {

	@Shadow
	@Final
	public Language languagePack;

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z",
			ordinal = 0
		)
	)
	private boolean spike$uniqueIdEquals(String settingsLanguageId, Object anObject) {
		String currentId = GameSettings.LANGUAGE.value;
		return LanguagePackSelectionState.isSelected(this.languagePack, currentId);
	}
}
