package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.useless.dragonfly.models.entity.mojang.StaticEntityModelMojang;

import java.nio.ByteBuffer;

@Mixin(StaticEntityModelMojang.TessellatorEntity.class)
public abstract class StaticEntityModelMojangTessellatorAlignment {

	@Unique
	private static final int VERT_SIZE = 32;

	@Shadow
	public ByteBuffer data;
	@Shadow
	public boolean drawing;
	@Shadow
	public int boneIndex;
	@Shadow
	public byte normalX;
	@Shadow
	public byte normalY;
	@Shadow
	public byte normalZ;
	@Shadow
	public int vertexCount;
	@Shadow
	private int quadVertCount;

	@Shadow
	public abstract void checkCanFit(int size);

	@WrapMethod(method = "addVertexWithUV(DDDDD)V")
	private void spike$alignedAddVertex(double x, double y, double z, double u, double v, Operation<Void> original) {
		if (!this.drawing) {
			throw new IllegalStateException("Entity Tessellator not drawing!");
		}
		this.checkCanFit(64);
		this.data.putFloat((float) x).putFloat((float) y).putFloat((float) z);
		this.data.putFloat((float) u).putFloat((float) v);
		this.data.put(this.normalX).put(this.normalY).put(this.normalZ);
		this.data.put((byte) 0);
		this.data.putFloat((float) this.boneIndex + 0.05f);
		this.data.putInt(0);
		++this.vertexCount;
		if (this.quadVertCount % 4 == 2) {
			this.checkCanFit(128);
			this.data.put(this.data.position(), this.data, this.data.position() - VERT_SIZE * 3, VERT_SIZE);
			this.data.put(this.data.position() + VERT_SIZE, this.data, this.data.position() - VERT_SIZE, VERT_SIZE);
			this.data.position(this.data.position() + VERT_SIZE * 2);
			this.vertexCount += 2;
		}
		++this.quadVertCount;
	}

	@WrapMethod(method = "enable")
	private void spike$alignedEnable(int vao, int vbo, Operation<Void> original) {
		GL41.glBindVertexArray(vao);
		GL41.glBindBuffer(34962, vbo);
		GL41.glVertexAttribPointer(0, 3, 5126, false, VERT_SIZE, 0L);
		GL41.glVertexAttribPointer(1, 2, 5126, false, VERT_SIZE, 12L);
		GL41.glVertexAttribPointer(2, 4, 5120, true, VERT_SIZE, 20L);
		GL41.glVertexAttribPointer(3, 1, 5126, false, VERT_SIZE, 24L);
		GL41.glEnableVertexAttribArray(0);
		GL41.glEnableVertexAttribArray(1);
		GL41.glEnableVertexAttribArray(2);
		GL41.glEnableVertexAttribArray(3);
	}
}
