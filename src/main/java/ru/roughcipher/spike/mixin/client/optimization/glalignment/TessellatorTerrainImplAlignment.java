package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.tessellator.TessellatorTerrainImpl;
import net.minecraft.core.util.helper.Color;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;

@Mixin(TessellatorTerrainImpl.class)
public abstract class TessellatorTerrainImplAlignment {

	@Shadow
	private double offsetX;
	@Shadow
	private double offsetY;
	@Shadow
	private double offsetZ;
	@Shadow
	private int color;
	@Shadow
	private byte normalX;
	@Shadow
	private byte normalY;
	@Shadow
	private byte normalZ;
	@Shadow
	private double textureU;
	@Shadow
	private double textureV;
	@Shadow
	private byte shade;
	@Shadow
	private byte lightIndex;
	@Shadow
	@Final
	private int vertSize;
	@Shadow
	private int quadVertCount;
	@Shadow
	@Final
	public TessellatorTerrainImpl.TerrainVertexData data;

	@Shadow
	private void checkIsDrawing() {
	}

	@WrapMethod(method = "addVertex")
	private void spike$alignedAddVertex(double x, double y, double z, Operation<Void> original) {
		this.checkIsDrawing();
		this.data.resizeIfNeeded(256);
		ByteBuffer buffer = this.data.getBuffer();
		buffer.putFloat((float) (this.offsetX + x))
			.putFloat((float) (this.offsetY + y))
			.putFloat((float) (this.offsetZ + z));
		buffer.put((byte) Color.blueFromInt(this.color))
			.put((byte) Color.greenFromInt(this.color))
			.put((byte) Color.redFromInt(this.color))
			.put((byte) 0);
		buffer.put(this.normalX).put(this.normalY).put(this.normalZ).put((byte) 0);
		buffer.putFloat((float) this.textureU).putFloat((float) this.textureV);
		buffer.put(this.shade).put(this.lightIndex).put((byte) 0).put((byte) 0);
		++this.data.vertexCount;
		if ((this.quadVertCount & 3) == 2) {
			buffer.put(buffer.position(), buffer, buffer.position() - this.vertSize * 3, this.vertSize);
			buffer.put(buffer.position() + this.vertSize, buffer, buffer.position() - this.vertSize, this.vertSize);
			buffer.position(buffer.position() + this.vertSize * 2);
			this.data.vertexCount += 2;
		}
		++this.quadVertCount;
	}
}
