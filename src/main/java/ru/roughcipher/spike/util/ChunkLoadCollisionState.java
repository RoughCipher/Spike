package ru.roughcipher.spike.util;

import net.minecraft.core.entity.Entity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class ChunkLoadCollisionState {

	private static final Map<Entity, State> STATES =
		Collections.synchronizedMap(new WeakHashMap<>());

	private ChunkLoadCollisionState() {
	}

	public static void markLoaded(Entity entity) {
		State state = STATES.computeIfAbsent(entity, e -> new State());
		state.postLoadRetries = 5;
		state.wallImmunity = 40;
	}

	public static int getPostLoadRetries(Entity entity) {
		State state = STATES.get(entity);
		return state == null ? 0 : state.postLoadRetries;
	}

	public static void consumePostLoadRetry(Entity entity) {
		State state = STATES.get(entity);
		if (state != null && state.postLoadRetries > 0) {
			state.postLoadRetries--;
			cleanupIfIdle(entity, state);
		}
	}

	public static void clearPostLoad(Entity entity) {
		State state = STATES.get(entity);
		if (state != null) {
			state.postLoadRetries = 0;
			cleanupIfIdle(entity, state);
		}
	}

	public static void grantWallImmunity(Entity entity, int ticks) {
		State state = STATES.computeIfAbsent(entity, e -> new State());
		if (state.wallImmunity < ticks) {
			state.wallImmunity = ticks;
		}
	}

	public static boolean hasWallImmunity(Entity entity) {
		State state = STATES.get(entity);
		return state != null && state.wallImmunity > 0;
	}

	public static void tickWallImmunity(Entity entity) {
		State state = STATES.get(entity);
		if (state != null && state.wallImmunity > 0) {
			state.wallImmunity--;
			cleanupIfIdle(entity, state);
		}
	}

	private static void cleanupIfIdle(Entity entity, State state) {
		if (state.postLoadRetries <= 0 && state.wallImmunity <= 0) {
			STATES.remove(entity);
		}
	}

	private static final class State {
		int postLoadRetries;
		int wallImmunity;
	}
}
