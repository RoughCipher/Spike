package ru.roughcipher.spike.client;

import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionString;
import net.minecraft.core.lang.Language;
import net.minecraft.core.lang.LanguageSeeker;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class LanguagePackSelectionState {

	public static final OptionString LANGUAGE_PACK_KEY =
		GameSettings.register(new OptionString("languagePackKey", ""));

	private LanguagePackSelectionState() {
	}

	private static final char SEP = '|';

	public static String makeKey(Language lang) {
		if (lang == null) {
			return "";
		}
		String credits = "";
		List<String> creditList = lang.getCredits();
		if (creditList != null && !creditList.isEmpty()) {
			credits = creditList.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.joining(","));
		}
		return escape(Objects.toString(lang.getName(), ""))
			+ SEP + escape(Objects.toString(lang.getRegion(), ""))
			+ SEP + escape(credits);
	}

	private static String escape(String s) {
		return s.replace("\\", "\\\\")
			.replace("|", "\\|")
			.replace("\n", "\\n")
			.replace("\r", "\\r");
	}

	@Nullable
	private static String getStoredKey() {
		String s = LANGUAGE_PACK_KEY.value;
		return s.isEmpty() ? null : s;
	}

	public static boolean isSelected(Language pack, @Nullable String currentLanguageId) {
		if (pack == null || currentLanguageId == null || !currentLanguageId.equals(pack.getId())) {
			return false;
		}
		String packKey = makeKey(pack);
		String key = getStoredKey();
		if (key == null) {
			LANGUAGE_PACK_KEY.set(packKey);
			return true;
		}
		return key.equals(packKey);
	}

	public static void setSelected(Language pack) {
		if (pack != null) {
			LANGUAGE_PACK_KEY.set(makeKey(pack));
			try {
				GameSettings.saveOptions();
			} catch (Exception ignored) {
			}
		}
	}

	public static boolean matchesSelected(Language pack) {
		if (pack == null) {
			return false;
		}
		String key = getStoredKey();
		return key != null && key.equals(makeKey(pack));
	}

	public static Language resolve(@Nullable String id) throws Exception {
		if (id == null || "en_US".equals(id)) {
			return LanguageSeeker.seek(id == null ? "en_US" : id);
		}
		List<Language> available = LanguageSeeker.getAvailableLanguages();
		Language firstMatch = null;
		for (Language lang : available) {
			if (!id.equals(lang.getId())) {
				continue;
			}
			if (firstMatch == null) {
				firstMatch = lang;
			}
			if (matchesSelected(lang)) {
				return lang;
			}
		}
		if (firstMatch != null) {
			if (getStoredKey() == null) {
				LANGUAGE_PACK_KEY.set(makeKey(firstMatch));
			}
			return firstMatch;
		}
		return LanguageSeeker.seek(id);
	}
}
