package ru.roughcipher.spike.mixin.core.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Mixin(PacketBlockRegionUpdate.class)
public abstract class PacketBlockRegionUpdateFast {

	@Unique
	private static final ThreadLocal<Deflater> SPIKE$DEFLATER =
			ThreadLocal.withInitial(() -> new Deflater(Deflater.BEST_SPEED));

	@Unique
	private static final ThreadLocal<Inflater> SPIKE$INFLATER =
			ThreadLocal.withInitial(Inflater::new);

	@WrapOperation(
		method = "<init>(IIIIIILnet/minecraft/core/world/World;)V",
		at = @At(
			value = "NEW",
			target = "java/util/zip/Deflater"
		)
	)
	private Deflater spike$pooledDeflater(int level, Operation<Deflater> original) {
		Deflater deflater = SPIKE$DEFLATER.get();
		deflater.reset();
		deflater.setLevel(Deflater.BEST_SPEED);
		return deflater;
	}

	@WrapOperation(
		method = "<init>(IIIIIILnet/minecraft/core/world/World;)V",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/zip/Deflater;end()V"
		)
	)
	private void spike$skipDeflaterEnd(Deflater deflater, Operation<Void> original) {
	}

	@WrapOperation(
		method = "read",
		at = @At(
			value = "NEW",
			target = "java/util/zip/Inflater"
		)
	)
	private Inflater spike$pooledInflater(Operation<Inflater> original) {
		Inflater inflater = SPIKE$INFLATER.get();
		inflater.reset();
		return inflater;
	}

	@WrapOperation(
		method = "read",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/zip/Inflater;end()V"
		)
	)
	private void spike$skipInflaterEnd(Inflater inflater, Operation<Void> original) {
	}
}
