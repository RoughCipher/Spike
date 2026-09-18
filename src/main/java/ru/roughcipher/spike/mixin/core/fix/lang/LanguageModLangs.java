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

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
		at = @At("TAIL")
	)
	private void spike$loadModLangsZip(String id, String name, String region, List<String> credits, ZipFile zipFile, CallbackInfo ci) {
		spike$loadMissingModTranslations();
	}

	@Inject(
		method = "<init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/io/File;)V",
		at = @At("TAIL")
	)
	private void spike$loadModLangsFile(String id, String name, String region, List<String> credits, File directory, CallbackInfo ci) {
		spike$loadMissingModTranslations();
	}

	@Unique
	private void spike$loadMissingModTranslations() {
		if ("en_US".equals(this.id)) {
			return;
		}

		for (String namespace : Registries.NAMESPACES) {
			String dir = "/assets/" + namespace + "/lang/" + this.id + "/";
			for (String path : I18n.getFilesInDirectory(dir)) {
				if (!path.endsWith(".lang")) {
					continue;
				}
				try (InputStream in = I18n.getResourceAsStream(path)) {
					if (in == null) {
						continue;
					}
					Properties props = new Properties();
					try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
						props.load(reader);
					}
					for (String key : props.stringPropertyNames()) {
						if (this.entries.getProperty(key) == null) {
							this.entries.setProperty(key, props.getProperty(key));
						}
					}
				} catch (Exception ignored) {
					// Skip broken files.
				}
			}
		}
	}
}
