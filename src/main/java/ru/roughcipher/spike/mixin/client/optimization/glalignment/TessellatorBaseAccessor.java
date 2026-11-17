package ru.roughcipher.spike.mixin.client.optimization.glalignment;

import net.minecraft.client.render.tessellator.TessellatorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TessellatorBase.class)
public interface TessellatorBaseAccessor {

	@Accessor("offsetX")
	double spike$getOffsetX();

	@Accessor("offsetY")
	double spike$getOffsetY();

	@Accessor("offsetZ")
	double spike$getOffsetZ();
}
