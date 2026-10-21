package ru.roughcipher.spike.config;

import ru.roughcipher.spike.Spike;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

public final class SpikeConfig {

	public static boolean spawnChunks = false;

	private SpikeConfig() {
	}

	public static void load() {
		Toml defaults = new Toml("Spike configuration file.");
		defaults.addEntry(
			"spawn-chunks",
			"""
			Prepare and keep overworld spawn area loaded.""",
			false
		);

		TomlConfigHandler config = new TomlConfigHandler(Spike.MOD_ID, defaults);
		spawnChunks = config.getBoolean("spawn-chunks");
	}
}
