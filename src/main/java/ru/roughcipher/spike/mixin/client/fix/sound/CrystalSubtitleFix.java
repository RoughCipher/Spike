package ru.roughcipher.spike.mixin.client.fix.sound;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.google.gson.JsonElement;
import net.minecraft.client.sound.NamedSoundRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NamedSoundRepository.class)
public abstract class CrystalSubtitleFix {

	@WrapOperation(
		method = "loadFromPack",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/gson/JsonElement;getAsString()Ljava/lang/String;"
		)
	)
	private String spike$fixCrystalSubtitleKey(JsonElement element, Operation<String> original) {
		String value = original.call(element);
		if ("subtitles.step.crystal".equals(value)) {
			return "subtitles.tile.crystal";
		}
		return value;
	}
}
