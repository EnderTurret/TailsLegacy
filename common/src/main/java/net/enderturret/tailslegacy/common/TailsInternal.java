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
		final Path newConfig = configDir.resolve("tailslegacy-client.toml");

		if (Files.exists(newConfig)) return;

		final Path oldConfig = configDir.resolve("tails-client.toml");

		if (!Files.exists(oldConfig)) return;

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

	public static String maybeMigrateCFGConfig(Path configDir) {
		final Path newConfig = configDir.resolve("TailsLegacy.cfg");

		if (Files.exists(newConfig)) return null;

		Path oldConfig = configDir.resolve("Tails.cfg");

		// Try the Tails 1.12 config name, if we can't find the 1.7 version or our own old one.
		if (!Files.exists(oldConfig)) oldConfig = configDir.resolve("tails.cfg");

		if (!Files.exists(oldConfig)) return null;

		try {
			boolean ourConfig = false;
			String match = null;

			for (String line : Files.readAllLines(oldConfig))
				if (line.contains("\"Hide Preview In Third Person\"")) {
					ourConfig = true;
				}
				else if (line.contains("\"Local Player Data\"")) {
					match = line.substring(line.indexOf('=') + 1);
					if (ourConfig) break;
				}
				else if (line.contains("\"Local Tail Info\"")) { // Backwards compatibility with early 1.7.10 versions of the original Tails.
					match = line.substring(line.indexOf('=') + 1);
					ourConfig = false;
					break;
				}

			if (match == null) return null;

			TailsPlatform.get().logInfo("Migrating Tails.cfg to TailsLegacy.cfg...");

			if (ourConfig) {
				Files.move(oldConfig, newConfig);
				return null;
			}

			return match;
		} catch (IOException e) {
			TailsPlatform.get().logError("Exception migrating config file:", e);
			return null;
		}
	}
}