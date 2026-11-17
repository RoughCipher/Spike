package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.tessellator.TessellatorParticleShader;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Mixin(TessellatorParticleShader.class)
public abstract class TessellatorParticleShaderAlignment {

	@Unique
	private static final int VERT_SIZE = 40;

	@Shadow
	private ByteBuffer buffer;
	@Shadow
	private double offsetX;
	@Shadow
	private double offsetY;
	@Shadow
	private double offsetZ;
	@Shadow
	private int color;
	@Shadow
	private byte lightIndex;
	@Shadow
	private int count;
	@Shadow
	@Final
	private int vao;
	@Shadow
	@Final
	private int vbo;

	@Shadow
	public abstract void checkIsDrawing();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void spike$realignAttribs(int size, CallbackInfo ci) {
		GL41.glBindVertexArray(this.vao);
		GL41.glBindBuffer(34962, this.vbo);
		int offset = 0;
		GL41.glVertexAttribPointer(1, 3, 5126, false, VERT_SIZE, offset);
		offset += 12;
		GL41.glVertexAttribPointer(2, 1, 5126, false, VERT_SIZE, offset);
		offset += 4;
		GL41.glVertexAttribPointer(3, 4, 5121, true, VERT_SIZE, offset);
		offset += 4;
		GL41.glVertexAttribPointer(4, 2, 5126, false, VERT_SIZE, offset);
		offset += 8;
		GL41.glVertexAttribPointer(5, 2, 5126, false, VERT_SIZE, offset);
		offset += 8;
		GL41.glVertexAttribPointer(6, 1, 5121, false, VERT_SIZE, offset);
		GL41.glBindBuffer(34962, 0);
		GL41.glBindVertexArray(0);
	}

	@WrapMethod(method = "addParticle")
	private void spike$alignedAddParticle(double x, double y, double z, float scale, double u, double v, double uSize, double vSize, Operation<Void> original) {
		this.checkIsDrawing();
		if (this.buffer.capacity() < this.buffer.position() + 64) {
			int newSize = this.buffer.capacity() * 2;
			ByteBuffer newBuffer = ByteBuffer.allocateDirect(newSize).order(ByteOrder.nativeOrder());
			this.buffer.flip();
			newBuffer.put(this.buffer);
			this.buffer = newBuffer;
		}
		this.buffer.putFloat((float) (x + this.offsetX))
			.putFloat((float) (y + this.offsetY))
			.putFloat((float) (z + this.offsetZ))
			.putFloat(scale);
		this.buffer.putInt(this.color);
		this.buffer.putFloat((float) u).putFloat((float) v);
		this.buffer.putFloat((float) uSize).putFloat((float) vSize);
		this.buffer.put(this.lightIndex);
		this.buffer.put((byte) 0).put((byte) 0).put((byte) 0);
		++this.count;
	}
}
