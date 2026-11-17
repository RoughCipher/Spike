package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.tessellator.TessellatorBase;
import net.minecraft.client.render.tessellator.TessellatorShader;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.ByteBuffer;

@Mixin(TessellatorShader.class)
public abstract class TessellatorShaderAlignment {

	@Shadow
	private int color;
	@Shadow
	private byte shade;
	@Shadow
	private float texU;
	@Shadow
	private float texV;
	@Shadow
	private byte lightIndex;
	@Shadow
	private byte normalX;
	@Shadow
	private byte normalY;
	@Shadow
	private byte normalZ;
	@Shadow
	public byte config;
	@Shadow
	private boolean quadEmulation;
	@Shadow
	private int quadVertCount;

	@Unique
	private static int spike$calcSize(boolean color, boolean texture, boolean lightmap, boolean normal) {
		int size = 12;
		if (color) size += 4;
		if (texture) size += 8;
		if (lightmap) size += 4;
		if (normal) size += 4;
		return size;
	}

	@ModifyReturnValue(method = "getVertexSize", at = @At("RETURN"))
	private int spike$alignedVertexSize(int original, boolean color, boolean texture, boolean lightmap, boolean normal) {
		return spike$calcSize(color, texture, lightmap, normal);
	}

	@WrapMethod(method = "addVertex")
	private void spike$alignedAddVertex(double x, double y, double z, Operation<Void> original) {
		TessellatorBase self = (TessellatorBase) (Object) this;
		TessellatorBaseAccessor acc = (TessellatorBaseAccessor) self;
		self.checkIsDrawing();
		self.checkCanFit(64);
		ByteBuffer data = self.data;
		data.putFloat((float) (acc.spike$getOffsetX() + x));
		data.putFloat((float) (acc.spike$getOffsetY() + y));
		data.putFloat((float) (acc.spike$getOffsetZ() + z));
		if ((this.config & 1) != 0) {
			byte a = (byte) ((this.color & 0xFF000000) >> 24);
			byte b = (byte) ((this.color & 0xFF0000) >> 16);
			byte g = (byte) ((this.color & 0xFF00) >> 8);
			byte r = (byte) (this.color & 0xFF);
			data.put((byte) (r * Byte.toUnsignedInt(this.shade) >> 8))
				.put((byte) (g * Byte.toUnsignedInt(this.shade) >> 8))
				.put((byte) (b * Byte.toUnsignedInt(this.shade) >> 8))
				.put(a);
		}
		if ((this.config & 2) != 0) {
			data.putFloat(this.texU);
			data.putFloat(this.texV);
		}
		if ((this.config & 4) != 0) {
			data.put(this.lightIndex);
			data.put((byte) 0).put((byte) 0).put((byte) 0);
		}
		if ((this.config & 8) != 0) {
			data.put(this.normalX).put(this.normalY).put(this.normalZ);
			data.put((byte) 0);
		}
		++self.vertexCount;
		if (this.quadEmulation && this.quadVertCount % 4 == 2) {
			int vertSize = spike$calcSize(
				(this.config & 1) != 0,
				(this.config & 2) != 0,
				(this.config & 4) != 0,
				(this.config & 8) != 0
			);
			self.checkCanFit(128);
			data.put(data.position(), data, data.position() - vertSize * 3, vertSize);
			data.put(data.position() + vertSize, data, data.position() - vertSize, vertSize);
			data.position(data.position() + vertSize * 2);
			self.vertexCount += 2;
		}
		++this.quadVertCount;
	}

	@WrapMethod(method = "enableVertexAttribs(IIZZZZ)V")
	private void spike$alignedEnable(int vao, int vbo, boolean color, boolean texture, boolean lightIndex, boolean normal, Operation<Void> original) {
		GL41.glBindVertexArray(vao);
		GL41.glBindBuffer(34962, vbo);
		int vertexSize = spike$calcSize(color, texture, lightIndex, normal);
		int offset = 0;
		GL41.glVertexAttribPointer(0, 3, 5126, false, vertexSize, offset);
		offset += 12;
		GL41.glEnableVertexAttribArray(0);
		if (color) {
			GL41.glVertexAttribPointer(1, 4, 5121, true, vertexSize, offset);
			offset += 4;
			GL41.glEnableVertexAttribArray(1);
		} else {
			GL41.glDisableVertexAttribArray(1);
		}
		if (texture) {
			GL41.glVertexAttribPointer(2, 2, 5126, false, vertexSize, offset);
			offset += 8;
			GL41.glEnableVertexAttribArray(2);
		} else {
			GL41.glDisableVertexAttribArray(2);
		}
		if (lightIndex) {
			GL41.glVertexAttribPointer(3, 1, 5121, false, vertexSize, offset);
			offset += 4;
			GL41.glEnableVertexAttribArray(3);
		} else {
			GL41.glDisableVertexAttribArray(3);
		}
		if (normal) {
			GL41.glVertexAttribPointer(4, 4, 5120, true, vertexSize, offset);
			GL41.glEnableVertexAttribArray(4);
		} else {
			GL41.glDisableVertexAttribArray(4);
		}
	}
}
