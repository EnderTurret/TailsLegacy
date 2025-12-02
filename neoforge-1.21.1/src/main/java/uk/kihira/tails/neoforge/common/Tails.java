/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

import uk.kihira.tails.neoforge.proxy.IProxy;
import uk.kihira.tails.neoforge.proxy.ServerProxy;

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

	@Internal
	public Tails(ModContainer mc) {
		mc.registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
	}
}