package ru.roughcipher.spike.mixin.client.fix.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.server.ServerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerManager.class)
public abstract class LanServerFormat {

	@WrapOperation(
		method = "onLocalScanned",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
		)
	)
	private String spike$formatHostPort(String format, Object[] args, Operation<String> original) {
		if (args != null && args.length >= 2 && args[0] instanceof String host) {
			String port = String.valueOf(args[1]);
			return spike$joinHostPort(host, port);
		}
		return original.call(format, args);
	}

	@Unique
	private static String spike$joinHostPort(String host, String port) {
		if (host.indexOf(':') >= 0 && !host.startsWith("[")) {
			return "[" + host + "]:" + port;
		}
		return host + ":" + port;
	}
}
