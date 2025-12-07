/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

/**
 * The Tails config, for all your configuration needs.
 * Configuration sold separately.
 * @author EnderTurret
 */
@Internal
public final class TailsConfig {

	static final ModConfigSpec CLIENT_SPEC;
	@Internal
	public static final TailsConfig CLIENT_INSTANCE;

	static {
		final Pair<TailsConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(TailsConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT_INSTANCE = pair.getLeft();
	}

	@Internal
	public final ConfigValue<String> localPlayerData;
	@Internal
	public final BooleanValue hidePreviewInThirdPerson;

	private TailsConfig(ModConfigSpec.Builder builder) {
		builder.push("client");

		hidePreviewInThirdPerson = builder
				.comment("Whether to hide the preview in the editor screen when in third person mode.")
				.define("hidePreviewInThirdPerson", true);

		localPlayerData = builder
				.comment("The local player's customization data. Editing this manually is discouraged.")
				.define("localPlayerData", "");
	}

	/**
	 * Returns the internal {@link ModConfigSpec}.
	 * @return The config.
	 */
	@Internal
	public static ModConfigSpec getConfig() {
		return CLIENT_SPEC;
	}
}