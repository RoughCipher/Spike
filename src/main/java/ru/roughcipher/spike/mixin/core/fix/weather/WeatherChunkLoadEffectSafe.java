package ru.roughcipher.spike.mixin.core.fix.weather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.pos.ChunkTilePos;
import net.minecraft.core.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Weather.class)
public abstract class WeatherChunkLoadEffectSafe {

	@WrapOperation(
		method = "doChunkLoadEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/chunk/Chunk;setBlockID(IIII)Z"
		)
	)
	private boolean spike$rawSetBlockDuringLoadEffect(
		Chunk chunk,
		int x,
		int y,
		int z,
		int id,
		Operation<Boolean> original
	) {
		return chunk.setBlockIdDataRaw(new ChunkTilePos(x, y, z), id, 0);
	}
}
