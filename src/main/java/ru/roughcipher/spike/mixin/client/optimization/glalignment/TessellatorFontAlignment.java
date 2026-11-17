package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.tessellator.TessellatorFont;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(TessellatorFont.class)
public abstract class TessellatorFontAlignment {

	@Unique
	private static final int VERT_SIZE = 20;

	@Shadow
	private long config;
	@Shadow
	private boolean shadow;
	@Shadow
	private ByteBuffer data;
	@Shadow
	private int vertexCount;

	@Shadow
	public abstract void checkIsDrawing();

	@Shadow
	public abstract void checkCanFit(int size);

	@WrapMethod(method = "setAttribOffset")
	private static void spike$alignedAttribOffset(int offset, Operation<Void> original) {
		int _offset = offset * VERT_SIZE;
		GL41.glVertexAttribPointer(1, 2, 5126, false, VERT_SIZE, _offset);
		_offset += 8;
		GL41.glVertexAttribPointer(2, 2, 5121, false, VERT_SIZE, _offset);
		_offset += 2;
		_offset += 2;
		GL41.glVertexAttribPointer(3, 4, 5121, true, VERT_SIZE, _offset);
		_offset += 4;
		GL41.glVertexAttribPointer(4, 2, 5121, false, VERT_SIZE, _offset);
		_offset += 2;
		GL41.glVertexAttribPointer(5, 1, 5121, false, VERT_SIZE, _offset);
	}

	@WrapMethod(method = "addChar")
	private void spike$alignedAddChar(double x, double y, int width, int height, int u, int v, Operation<Void> original) {
		this.checkIsDrawing();
		this.checkCanFit(64);
		this.data.putFloat((float) x);
		this.data.putFloat((float) y);
		this.data.put((byte) width);
		this.data.put((byte) height);
		this.data.put((byte) 0).put((byte) 0);

		long color = this.config & 0xFFFFFFFFL;
		byte a = (byte) ((color & 0xFF000000L) >> 24);
		byte r = (byte) ((color & 0xFF0000L) >> 16);
		byte g = (byte) ((color & 0xFF00L) >> 8);
		byte b = (byte) (color & 0xFFL);
		if (a == 0) {
			a = -1;
		}
		this.data.put(r).put(g).put(b).put(a);
		this.data.put((byte) u);
		this.data.put((byte) v);
		byte packedConfig = (byte) (this.config >> 32 | (long) (this.shadow ? 128 : 0));
		this.data.put(packedConfig);
		this.data.put((byte) 0);
		++this.vertexCount;
	}
}
