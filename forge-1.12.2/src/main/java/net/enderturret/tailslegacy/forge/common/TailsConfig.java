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

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;

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

		final String data = TailsInternal.maybeMigrateCFGConfig(configDir.toPath(), false);

		config = new Configuration(new File(configDir, "TailsLegacy.cfg"));

		hidePreviewInThirdPerson = config.getBoolean("Hide Preview In Third Person", Configuration.CATEGORY_GENERAL, true,
				"Whether to hide the preview in the editor screen when in third person mode.");

		localPlayerData = config.getString("Local Player Data", Configuration.CATEGORY_GENERAL, "",
				"The local player's customization data. Editing this manually is discouraged.");

		if (data != null) {
			TailsPlatform.get().logInfo("Found old customization data, migrating!\n{}", data);
			localPlayerData = data;
			config.getCategory(Configuration.CATEGORY_GENERAL).get("Local Player Data").set(data);
		}

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