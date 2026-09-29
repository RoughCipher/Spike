package ru.roughcipher.spike;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class SpikePreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		if (System.getProperty("log4j2.statusLoggerLevel") == null) {
			System.setProperty("log4j2.statusLoggerLevel", "ERROR");
		}
	}
}
