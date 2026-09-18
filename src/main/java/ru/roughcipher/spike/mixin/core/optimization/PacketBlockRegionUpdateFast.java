package ru.roughcipher.spike.mixin.core.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.zip.Deflater;

@Mixin(PacketBlockRegionUpdate.class)
public abstract class PacketBlockRegionUpdateFast {

	@WrapOperation(
		method = "<init>(IIIIIILnet/minecraft/core/world/World;)V",
		at = @At(
			value = "NEW",
			target = "java/util/zip/Deflater"
		)
	)
	private Deflater spike$Compression(int level, Operation<Deflater> original) {
		return new Deflater(Deflater.BEST_SPEED);
	}
}
