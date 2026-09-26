package ru.roughcipher.spike.mixin.client.fix.lang;

import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.roughcipher.spike.client.LanguagePackSelectionState;

@Mixin(I18n.class)
public abstract class I18nPreferSelectedPack {

	@Redirect(
		method = "reload",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/lang/LanguageSeeker;seek(Ljava/lang/String;)Lnet/minecraft/core/lang/Language;"
		)
	)
	private Language spike$seekPreferred(String id) throws Exception {
		return LanguagePackSelectionState.resolve(id);
	}
}
