package ru.roughcipher.spike.mixin.client.fix.auth;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class AccessTokenLaunchArg {

	@Inject(method = "main", at = @At("HEAD"))
	private static void spike$AccessToken(String[] args, CallbackInfo ci) {
		if (args == null || args.length == 0) {
			return;
		}

		int sessionFlag = -1;
		int sessionValue = -1;
		int tokenFlag = -1;
		int tokenValue = -1;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--session".equals(arg)) {
				sessionFlag = i;
				if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
					sessionValue = i + 1;
				}
			} else if ("--accessToken".equals(arg)) {
				tokenFlag = i;
				if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
					tokenValue = i + 1;
				}
			}
		}

		if (tokenFlag < 0) {
			return;
		}

		boolean sessionOk = sessionFlag >= 0
				&& sessionValue >= 0
				&& !args[sessionValue].isEmpty();

		if (sessionOk) {
			return;
		}

		boolean tokenOk = tokenValue >= 0 && !args[tokenValue].isEmpty();
		if (!tokenOk) {
			return;
		}

		if (sessionFlag >= 0) {
			if (sessionValue >= 0) {
				args[sessionValue] = args[tokenValue];
			} else {
				args[tokenFlag] = "--session";
			}
		} else {
			args[tokenFlag] = "--session";
		}
	}
}
