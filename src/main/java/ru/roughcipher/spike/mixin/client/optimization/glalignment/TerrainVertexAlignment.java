package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.tessellator.TessellatorTerrainImpl;
import net.minecraft.client.render.terrain.VertexConfig;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TessellatorTerrainImpl.TerrainVertexConfig.class)
public abstract class TerrainVertexAlignment implements VertexConfig<TessellatorTerrainImpl.TerrainVertexConfig> {

	@Unique
	private static final int VERT_SIZE = 32;

	@ModifyReturnValue(method = "getVertexSize", at = @At("RETURN"))
	private int spike$alignedSize(int original) {
		return VERT_SIZE;
	}

	@WrapMethod(method = "enable")
	private void spike$alignedEnable(int vao, int vbo, Operation<Void> original) {
		GL41.glBindVertexArray(vao);
		GL41.glBindBuffer(34962, vbo);
		GL41.glVertexAttribPointer(0, 3, 5126, false, VERT_SIZE, 0L);
		GL41.glVertexAttribPointer(1, 3, 5121, true, VERT_SIZE, 12L);
		GL41.glVertexAttribPointer(2, 4, 5120, true, VERT_SIZE, 16L);
		GL41.glVertexAttribPointer(3, 2, 5126, false, VERT_SIZE, 20L);
		GL41.glVertexAttribPointer(4, 2, 5121, false, VERT_SIZE, 28L);
		GL41.glEnableVertexAttribArray(0);
		GL41.glEnableVertexAttribArray(1);
		GL41.glEnableVertexAttribArray(2);
		GL41.glEnableVertexAttribArray(3);
		GL41.glEnableVertexAttribArray(4);
	}
}
