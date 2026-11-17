package ru.roughcipher.spike.mixin.client.optimization;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.terrain.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VertexBuffer.class)
public abstract class VertexBufferDynamicDraw {

	@ModifyExpressionValue(
		method = {"<init>", "expandVBO"},
		at = @At(value = "CONSTANT", args = "intValue=35044")
	)
	private int spike$useDynamicDraw(int original) {
		return 35048;
	}
}
