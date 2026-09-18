package ru.roughcipher.spike.mixin.client.fix.net;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.net.packet.PacketServerIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.DataInputStream;
import java.io.IOException;

@Mixin(PacketServerIcon.class)
public abstract class PacketServerIconReadFully {

	@WrapOperation(
		method = "read",
		at = @At(
			value = "INVOKE",
			target = "Ljava/io/DataInputStream;read([B)I"
		)
	)
	private int spike$readFully(DataInputStream stream, byte[] b, Operation<Integer> original) throws IOException {
		stream.readFully(b);
		return b.length;
	}
}
