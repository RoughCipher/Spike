package ru.roughcipher.spike.config;

import ru.roughcipher.spike.Spike;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

public final class SpikeConfig {

	public static boolean spawnChunks = false;
	public static int simulationDistance = 8;

	private SpikeConfig() {
	}

	public static void load() {
		Toml defaults = new Toml("Spike configuration file.");
		defaults.addEntry(
			"spawn-chunks",
			"""
			Server only.
			Prepare and keep overworld spawn area loaded.""",
			false
		);
		defaults.addEntry(
			"simulation-distance",
			"""
			Client&Server.
			Chunk radius around each player for random ticks, weather, cave sounds
			and mob spawn. Default 8. Range 2-16.""",
			8
		);

		TomlConfigHandler config = new TomlConfigHandler(Spike.MOD_ID, defaults);
		spawnChunks = config.getBoolean("spawn-chunks");

		int value = config.getInt("simulation-distance");
		if (value < 2) {
			value = 2;
		} else if (value > 16) {
			value = 16;
		}
		simulationDistance = value;
	}
}
