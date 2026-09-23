package ru.roughcipher.spike.mixin.core.fix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Block.class, remap = false)
public abstract class SpectatorNoBlockCollision {

	@Unique
	private static boolean spike$isSpectator(Entity entity) {
		if (!(entity instanceof Player player)) {
			return false;
		}
		Gamemode gm = player.getGamemode();
		return gm == Gamemodes.SPECTATOR;
	}

	@WrapMethod(
		method = "onEntityCollision(Lnet/minecraft/core/world/World;Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/entity/Entity;)V"
	)
	private void spike$skipSpectatorBlockCollision(
		World world,
		TilePosc tilePos,
		Entity entity,
		Operation<Void> original
	) {
		if (spike$isSpectator(entity)) {
			return;
		}
		original.call(world, tilePos, entity);
	}
}
