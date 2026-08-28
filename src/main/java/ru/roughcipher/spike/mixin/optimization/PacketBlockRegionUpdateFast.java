package ru.roughcipher.spike.mixin.optimization;

import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.zip.Deflater;

@Mixin(PacketBlockRegionUpdate.class)
public abstract class PacketBlockRegionUpdateFast {

	@Redirect(
		method = "<init>(IIIIIILnet/minecraft/core/world/World;)V",
		at = @At(
			value = "NEW",
			target = "java/util/zip/Deflater"
		)
	)
	private Deflater spike$Compression(int level) {
		return new Deflater(Deflater.BEST_SPEED);
	}
}
