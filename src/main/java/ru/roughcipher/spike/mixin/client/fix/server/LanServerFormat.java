package ru.roughcipher.spike.mixin.client.fix.server;

import net.minecraft.client.gui.server.ServerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerManager.class)
public abstract class LanServerFormat {

	@Redirect(
		method = "onLocalScanned",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
		)
	)
	private String spike$formatHostPort(String format, Object[] args) {
		if (args != null && args.length >= 2 && args[0] instanceof String host) {
			String port = String.valueOf(args[1]);
			return spike$joinHostPort(host, port);
		}
		return String.format(format, args);
	}

	@Unique
	private static String spike$joinHostPort(String host, String port) {
		if (host.indexOf(':') >= 0 && !host.startsWith("[")) {
			return "[" + host + "]:" + port;
		}
		return host + ":" + port;
	}
}
