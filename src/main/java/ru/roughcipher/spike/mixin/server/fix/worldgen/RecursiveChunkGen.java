package ru.roughcipher.spike.mixin.server.fix.worldgen;

import net.minecraft.core.world.chunk.ChunkLoader;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProviderServer.class)
public abstract class RecursiveChunkGen {

	@Shadow
	public boolean chunkLoadOverride;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$init(
		WorldServer world,
		ChunkLoader chunkLoader,
		ChunkGenerator chunkGenerator,
		CallbackInfo ci
	) {
		this.chunkLoadOverride = true;
	}

	@Redirect(
		method = "provideChunk",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/world/chunk/provider/ChunkProviderServer;decorating:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private boolean spike$allowTerrainDuringDecorate(ChunkProviderServer self) {
		return false;
	}
}
