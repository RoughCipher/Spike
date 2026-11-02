package ru.roughcipher.spike.mixin.core.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.NextTickListEntry;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.PriorityQueue;
import java.util.TreeSet;

@Mixin(World.class)
public abstract class OptimizeNextTickList {

	@Unique
	private static final TreeSet<NextTickListEntry> SPIKE$DEAD_TREE_SET = new TreeSet<>();

	@Unique
	private final PriorityQueue<NextTickListEntry> spike$tickQueue = new PriorityQueue<>(1024);

	@WrapOperation(
		method = "<init>(Lnet/minecraft/core/world/Dimension;Lnet/minecraft/core/world/save/LevelStorage;Lnet/minecraft/core/world/settings/WorldConfiguration;Lnet/minecraft/core/world/save/LevelData;Lnet/minecraft/core/world/save/DimensionData;)V",
		at = @At(value = "NEW", target = "()Ljava/util/TreeSet;")
	)
	private static TreeSet<NextTickListEntry> spike$skipTreeSetAlloc(
		Operation<TreeSet<NextTickListEntry>> original
	) {
		return SPIKE$DEAD_TREE_SET;
	}

	@WrapOperation(
		method = "scheduleBlockUpdate(Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/block/Block;J)V",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/TreeSet;add(Ljava/lang/Object;)Z"
		)
	)
	private boolean spike$queueAdd(
		TreeSet<NextTickListEntry> ignored,
		Object e,
		Operation<Boolean> original
	) {
		return this.spike$tickQueue.add((NextTickListEntry) e);
	}

	@WrapOperation(
		method = "tickPendingTicks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/TreeSet;size()I"
		)
	)
	private int spike$queueSize(
		TreeSet<NextTickListEntry> ignored,
		Operation<Integer> original
	) {
		return this.spike$tickQueue.size();
	}

	@WrapOperation(
		method = "tickPendingTicks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/TreeSet;first()Ljava/lang/Object;"
		)
	)
	private Object spike$queuePeek(
		TreeSet<NextTickListEntry> ignored,
		Operation<Object> original
	) {
		return this.spike$tickQueue.peek();
	}

	@WrapOperation(
		method = "tickPendingTicks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/TreeSet;remove(Ljava/lang/Object;)Z"
		)
	)
	private boolean spike$queuePoll(
		TreeSet<NextTickListEntry> ignored,
		Object o,
		Operation<Boolean> original
	) {
		NextTickListEntry head = this.spike$tickQueue.poll();
		return head != null && head.equals(o);
	}

	@WrapOperation(
		method = "tickPendingTicks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/TreeSet;isEmpty()Z"
		)
	)
	private boolean spike$queueIsEmpty(
		TreeSet<NextTickListEntry> ignored,
		Operation<Boolean> original
	) {
		return this.spike$tickQueue.isEmpty();
	}
}
