package ru.roughcipher.spike.mixin.server.fix;

import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Mixin(PlayerList.class)
public abstract class PlayerListIpBan {

	@Redirect(
		method = "getPlayerForLogin",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Object;toString()Ljava/lang/String;"
		)
	)
	private String spike$extractHostAddress(Object address) {
		if (address instanceof InetSocketAddress isa) {
			InetAddress inet = isa.getAddress();
			if (inet != null) {
				String host = inet.getHostAddress();
				int zone = host.indexOf('%');
				if (zone >= 0) {
					host = host.substring(0, zone);
				}
				return host.toLowerCase();
			}
			String host = isa.getHostString();
			if (host != null) {
				return host.toLowerCase();
			}
		}
		if (address instanceof SocketAddress) {
			return spike$parseHostFallback(address.toString()).toLowerCase();
		}
		return address.toString();
	}

	@Redirect(
		method = "getPlayerForLogin",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/String;indexOf(Ljava/lang/String;)I",
			ordinal = 0
		)
	)
	private int spike$skipSlashStrip(String self, String str) {
		return -1;
	}

	@Redirect(
		method = "getPlayerForLogin",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/String;indexOf(Ljava/lang/String;)I",
			ordinal = 1
		)
	)
	private int spike$skipPortStrip(String self, String str) {
		return self.length();
	}

	@Unique
	private static String spike$parseHostFallback(String s) {
		if (s.startsWith("/")) {
			s = s.substring(1);
		}
		if (s.startsWith("[")) {
			int end = s.indexOf(']');
			if (end > 0) {
				return s.substring(1, end);
			}
		}
		int lastColon = s.lastIndexOf(':');
		if (lastColon > 0 && s.indexOf(':') == lastColon) {
			return s.substring(0, lastColon);
		}
		if (lastColon > 0) {
			String tail = s.substring(lastColon + 1);
			boolean allDigits = !tail.isEmpty();
			for (int i = 0; i < tail.length(); i++) {
				if (!Character.isDigit(tail.charAt(i))) {
					allDigits = false;
					break;
				}
			}
			if (allDigits) {
				return s.substring(0, lastColon);
			}
		}
		return s;
	}
}
