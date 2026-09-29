package ru.roughcipher.spike.util;

import net.minecraft.client.render.OpenGLHelper;

public final class ShaderVersionUtil {
	private ShaderVersionUtil() {}

	public static String downgrade(String source) {
		if (source == null || !needsDowngrade()) {
			return source;
		}
		String out = source
			.replace("#version 410 core", "#version 330 core")
			.replace("#version 410", "#version 330 core");
		out = out.replaceAll("(\\([^)]*&[^)]*\\))\\s*!=\\s*0\\b", "$1 != 0u");
		out = out.replaceAll("(\\([^)]*&[^)]*\\))\\s*==\\s*0\\b", "$1 == 0u");
		return out;
	}

	public static boolean needsDowngrade() {
		if (!OpenGLHelper.contextCreated) {
			return false;
		}
		int major = OpenGLHelper.major;
		int minor = OpenGLHelper.minor;
		return major < 4 || (major == 4 && minor < 1);
	}
}
