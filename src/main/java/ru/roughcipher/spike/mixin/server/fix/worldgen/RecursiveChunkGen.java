package ru.roughcipher.spike.mixin.server.fix.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkLoader;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProviderServer.class)
public abstract class RecursiveChunkGen {

	@Shadow
	public boolean chunkLoadOverride;

	@Final
	@Shadow
	private WorldServer world;

	@Unique
	private boolean spike$isNether() {
		return this.world != null && this.world.getDimension() == Dimension.NETHER;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$init(
		WorldServer world,
		ChunkLoader chunkLoader,
		ChunkGenerator chunkGenerator,
		CallbackInfo ci
	) {
		if (!spike$isNether()) {
			this.chunkLoadOverride = true;
		}
	}

	@WrapOperation(
		method = "provideChunk",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/world/chunk/provider/ChunkProviderServer;decorating:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private boolean spike$allowRecursiveExceptNether(ChunkProviderServer self, Operation<Boolean> original) {
		if (spike$isNether()) {
			return original.call(self);
		}
		return false;
	}
}
