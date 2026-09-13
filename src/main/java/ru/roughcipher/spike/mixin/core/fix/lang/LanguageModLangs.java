package ru.roughcipher.spike.mixin.core.fix.lang;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.zip.ZipFile;

@Mixin(Language.class)
public abstract class LanguageModLangs {

	@Final
	@Shadow
	protected Properties entries;

	@Final
	@Shadow
	private String id;

	@Inject(
		method = "<init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/zip/ZipFile;)V",
		at = @At("RETURN")
	)
	private void spike$loadModLangs(String id, String name, String region, java.util.List<String> credits, ZipFile zipFile, CallbackInfo ci) {
		if (((Language) (Object) this).isDefault()) {
			return;
		}

		for (String namespace : Registries.NAMESPACES) {
			spike$loadNamespace(namespace);
		}
	}

	@Unique
	private void spike$loadNamespace(String namespace) {
		String dir = "/assets/" + namespace + "/lang/" + this.id + "/";
		for (String file : I18n.getFilesInDirectory(dir)) {
			if (!file.endsWith(".lang")) {
				continue;
			}
			try (InputStream in = I18n.getResourceAsStream(file)) {
				if (in == null) {
					continue;
				}
				try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
					this.entries.load(reader);
				}
			} catch (Exception e) {
				// Skip broken files.
			}
		}
	}
}
