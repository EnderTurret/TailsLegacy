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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * The Tails config, for all your configuration needs.
 * @author EnderTurret
 */
public class TailsConfig {

	/**
	 * Now with more spicy reflection.
	 */
	private static final MethodHandle CONFIGTRACKER_CONFIGSBYMOD;

	static final ForgeConfigSpec CLIENT_SPEC;
	public static final TailsConfig CLIENT_INSTANCE;

	static {
		final Pair<TailsConfig,ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(TailsConfig::new);
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
	public final BooleanValue enableLibrary;
	public final BooleanValue forceLegacyRendering;

	private TailsConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client");

		localPlayerOutfit = builder.comment("Local Players outfit. Delete to remove all customisation data. Do not try to edit manually.").define("localPlayerOutfit", "");
		enableLibrary = builder.comment("Whether to enable the library system for sharing tails. This mostly matters on servers.").define("enableLibrary", true);
		forceLegacyRendering = builder.comment("Forces the legacy renderer which may have better compatibility with other mods.").define("forceLegacyRendering", false);
	}

	/**
	 * Returns a TailsConfig registered for the given type.<br>
	 * Currently, there's only a config for {@link net.minecraftforge.fml.config.ModConfig.Type#CLIENT Type.CLIENT}.
	 * @param type The type.
	 * @return The config.
	 */
	@Nullable
	public static ModConfig getConfig(ModConfig.Type type) {
		try {
			final Map<String, Map<ModConfig.Type, ModConfig>> configsByMod = (Map<String, Map<Type, ModConfig>>) CONFIGTRACKER_CONFIGSBYMOD.invoke(ConfigTracker.INSTANCE);
			final Map<ModConfig.Type,ModConfig> modConfigs = configsByMod.get(Tails.MOD_ID);
			if (modConfigs == null) return null;
			return modConfigs.get(type);
		} catch (Throwable e) {
			return null;
		}
	}
}