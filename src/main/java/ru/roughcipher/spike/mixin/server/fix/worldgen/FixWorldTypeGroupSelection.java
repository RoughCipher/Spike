package ru.roughcipher.spike.mixin.server.fix.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypeGroups;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//TODO: Удалить при обновлении.
@Mixin(MinecraftServer.class)
public abstract class FixWorldTypeGroupSelection {

	@WrapOperation(
		method = "initWorld",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/type/WorldTypeGroups$Group;get(Lnet/minecraft/core/world/Dimension;)Lnet/minecraft/core/world/type/WorldType;"
		)
	)
	private WorldType spike$matchGroupByOverworldType(
		WorldTypeGroups.Group group,
		Dimension dimension,
		Operation<WorldType> original
	) {
		return original.call(group, Dimension.OVERWORLD);
	}
}
