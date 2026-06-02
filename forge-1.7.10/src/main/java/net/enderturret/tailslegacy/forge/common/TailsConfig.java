/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common;

import java.io.File;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.common.config.Configuration;

/**
 * The Tails config, for all your configuration needs.
 * Configuration sold separately.
 * @author EnderTurret
 */
@Internal
public final class TailsConfig {

	@Internal
	public static final TailsConfig CLIENT_INSTANCE = new TailsConfig();

	private File configDir;
	private Configuration config;

	private boolean hidePreviewInThirdPerson;
	private String localPlayerData;

	private TailsConfig() {}

	public void load(File configDir) {
		this.configDir = configDir;
		config = new Configuration(new File(configDir, "Tails.cfg"));

		hidePreviewInThirdPerson = config.getBoolean("Hide Preview In Third Person", Configuration.CATEGORY_GENERAL, true,
				"Whether to hide the preview in the editor screen when in third person mode.");

		// Backwards compatibility with early 1.7.10 versions of the original Tails.
		// I suspect none of those builds are in use any more, but reading this field is practically free.
		config.renameProperty(Configuration.CATEGORY_GENERAL, "Local Tail Info", "Local Player Data");

		localPlayerData = config.getString("Local Player Data", Configuration.CATEGORY_GENERAL, "",
				"The local player's customization data. Editing this manually is discouraged.");

		// Just in case it's present.
		config.getCategory(Configuration.CATEGORY_GENERAL).remove("Enable Library");

		if (config.hasChanged())
			config.save();
	}

	public void save() {
		config.save();
	}

	public File configDir() {
		return configDir;
	}

	public boolean hidePreviewInThirdPerson() {
		return hidePreviewInThirdPerson;
	}

	public String localPlayerData() {
		return localPlayerData;
	}

	public void setLocalPlayerData(String value) {
		localPlayerData = value;
		config.getCategory(Configuration.CATEGORY_GENERAL).get("Local Player Data").set(value);
	}
}