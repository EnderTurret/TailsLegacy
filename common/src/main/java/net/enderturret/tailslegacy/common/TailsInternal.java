/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.ApiStatus.Internal;

public final class TailsInternal {

	static TailsPlatform platform;

	/**
	 * Whether to enable network debugging features, such as printing received packet data to the log.
	 */
	@Internal
	public static final boolean DEBUG_NETWORK = Boolean.getBoolean("tailslegacy.debugNetwork");

	public static void maybeMigrateTomlConfig(Path configDir) {
		final Path oldConfig = configDir.resolve("tails-client.toml");
		final Path newConfig = configDir.resolve("tailslegacy-client.toml");

		if (Files.exists(newConfig) || !Files.exists(oldConfig)) return;

		try {
			boolean matched = false;
			for (String line : Files.readAllLines(oldConfig))
				if (line.contains("hidePreviewInThirdPerson")) {
					matched = true;
					break;
				}

			if (!matched) return;

			TailsPlatform.get().logInfo("Migrating tails-client.toml to tailslegacy-client.toml...");

			Files.move(oldConfig, newConfig);
		} catch (IOException e) {
			TailsPlatform.get().logError("Exception migrating config file:", e);
		}
	}
}