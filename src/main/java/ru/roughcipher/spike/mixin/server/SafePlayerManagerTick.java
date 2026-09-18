package ru.roughcipher.spike.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.player.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@Mixin(PlayerManager.class)
public abstract class SafePlayerManagerTick {

	@Unique
	private static Object[] SNAPSHOT = new Object[128];

	@Unique
	private static final ArrayIt IT = new ArrayIt();

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
		)
	)
	private Iterator<?> spike$iterateSnapshot(List<?> list, Operation<Iterator<?>> original) {
		int size = list.size();
		if (size == 0) {
			return Collections.emptyIterator();
		}

		if (SNAPSHOT.length < size) {
			SNAPSHOT = new Object[Math.max(size, SNAPSHOT.length << 1)];
		}

		list.toArray(SNAPSHOT);
		IT.reset(SNAPSHOT, size);
		return IT;
	}

	@Unique
	private static final class ArrayIt implements Iterator<Object> {
		private Object[] arr;
		private int size;
		private int index;

		void reset(Object[] arr, int size) {
			this.arr = arr;
			this.size = size;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public Object next() {
			if (index >= size) {
				throw new NoSuchElementException();
			}
			return arr[index++];
		}
	}
}
