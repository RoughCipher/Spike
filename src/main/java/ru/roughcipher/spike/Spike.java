package ru.roughcipher.spike;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spike implements ModInitializer {
	public static final String MOD_ID = "spike";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Spike initialized.");
	}
}
