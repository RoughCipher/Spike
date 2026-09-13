package ru.roughcipher.spike.mixin.client.fix.sound;

import net.minecraft.client.sound.NamedSoundRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NamedSoundRepository.class)
public abstract class CrystalSubtitleFix {

	@Redirect(
		method = "loadFromPack",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/gson/JsonElement;getAsString()Ljava/lang/String;"
		)
	)
	private String spike$fixCrystalSubtitleKey(com.google.gson.JsonElement element) {
		String value = element.getAsString();
		if ("subtitles.step.crystal".equals(value)) {
			return "subtitles.tile.crystal";
		}
		return value;
	}
}
