package ru.roughcipher.spike.mixin.client.fix.server;

import net.minecraft.client.gui.server.ServerListenerThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.Inet6Address;

@Mixin(ServerListenerThread.class)
public abstract class LanServerAddress {

	@Redirect(
		method = "receivedLocalServer",
		at = @At(
			value = "INVOKE",
			target = "Ljava/net/InetAddress;getHostAddress()Ljava/lang/String;"
		)
	)
	private String spike$normalizeLanHost(InetAddress address) {
		return spike$cleanHost(address);
	}

	@Unique
	private static String spike$cleanHost(InetAddress address) {
		if (address instanceof Inet6Address inet6) {
			byte[] bytes = inet6.getAddress();
			if (spike$isIpv4Mapped(bytes)) {
				return String.format(
					"%d.%d.%d.%d",
					bytes[12] & 0xff,
					bytes[13] & 0xff,
					bytes[14] & 0xff,
					bytes[15] & 0xff
				);
			}
		}
		String host = address.getHostAddress();
		int zone = host.indexOf('%');
		if (zone >= 0) {
			host = host.substring(0, zone);
		}
		return host;
	}

	@Unique
	private static boolean spike$isIpv4Mapped(byte[] bytes) {
		if (bytes.length != 16) {
			return false;
		}
		for (int i = 0; i < 10; i++) {
			if (bytes[i] != 0) {
				return false;
			}
		}
		return bytes[10] == (byte) 0xff && bytes[11] == (byte) 0xff;
	}
}
