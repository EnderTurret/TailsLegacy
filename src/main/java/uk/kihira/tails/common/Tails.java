/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.part.IPartInfo;
import uk.kihira.tails.common2.part.PartsData;
import uk.kihira.tails.common_gson.LibraryEntryDataSerializer;
import uk.kihira.tails.common_gson.LoggingExclusionStrategy;
import uk.kihira.tails.common_gson.PartsDataSerializer;
import uk.kihira.tails.common_gson.ServerPartInfoSerializer;
import uk.kihira.tails.proxy.IProxy;
import uk.kihira.tails.proxy.ServerProxy;

/**
 * Look! It's the main mod file!
 */
@Mod(Tails.MOD_ID)
public final class Tails {

	/**
	 * It's <strike>a snow poff</strike> the mod id.
	 */
	public static final String MOD_ID = "tails";

	/**
	 * It's <strike>another snow poff</strike> the mod's logger.
	 */
	@Internal
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Internal
	public static final IProxy PROXY;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT)
			PROXY = IProxy.makeClientProxy();
		else
			PROXY = new ServerProxy();
	}

	/**
	 * A nice {@link Gson} instance for deserializing {@link PartsData}, among other things.
	 */
	public static final Gson SERVER_GSON = new GsonBuilder()
			.setExclusionStrategies(new LoggingExclusionStrategy())
			.registerTypeHierarchyAdapter(PartsData.class, new PartsDataSerializer())
			.registerTypeHierarchyAdapter(IPartInfo.class, ServerPartInfoSerializer.INSTANCE)
			.registerTypeAdapter(LibraryEntryData.class, new LibraryEntryDataSerializer())
			.create();

	@Internal
	public Tails(ModContainer mc) {
		mc.registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
	}
}