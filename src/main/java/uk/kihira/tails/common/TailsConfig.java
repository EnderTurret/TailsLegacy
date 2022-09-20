/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * The Tails config, for all your configuration needs.
 * @author EnderTurret
 */
public final class TailsConfig {

	/**
	 * Now with more spicy reflection.
	 */
	private static final MethodHandle CONFIGTRACKER_CONFIGSBYMOD;

	static final ForgeConfigSpec CLIENT_SPEC;
	public static final TailsConfig CLIENT_INSTANCE;

	private static ModConfig instance;

	static {
		final Pair<TailsConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(TailsConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT_INSTANCE = pair.getLeft();

		try {
			final Field field = ObfuscationReflectionHelper.findField(ConfigTracker.class, "configsByMod");
			CONFIGTRACKER_CONFIGSBYMOD = MethodHandles.publicLookup().unreflectGetter(field);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public final ConfigValue<String> localPlayerOutfit;
	public final BooleanValue forceLegacyRendering;

	private TailsConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client");

		localPlayerOutfit = builder.comment("Local Players outfit. Delete to remove all customisation data. Do not try to edit manually.").define("localPlayerOutfit", "");
		forceLegacyRendering = builder.comment("Forces the legacy renderer which may have better compatibility with other mods.").define("forceLegacyRendering", false);
	}

	/**
	 * Returns the internal {@link ModConfig}.
	 * @return The config.
	 */
	@Nullable
	public static ModConfig getConfig() {
		if (instance == null)
			try {
				final Map<String, Map<ModConfig.Type, ModConfig>> configsByMod = (Map<String, Map<Type, ModConfig>>) CONFIGTRACKER_CONFIGSBYMOD.invoke(ConfigTracker.INSTANCE);
				final Map<ModConfig.Type, ModConfig> modConfigs = configsByMod.get(Tails.MOD_ID);
				if (modConfigs == null) return null;
				instance = modConfigs.get(ModConfig.Type.CLIENT);
			} catch (Throwable e) {
				return null;
			}

		return instance;
	}
}