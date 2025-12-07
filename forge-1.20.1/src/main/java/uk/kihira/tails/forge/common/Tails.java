/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.proxy.IProxy;
import uk.kihira.tails.common.proxy.ServerProxy;

/**
 * Look! It's the main mod file!
 */
@Mod(TailsPlatform.MOD_ID)
public final class Tails {

	@Internal
	public static final Logger LOGGER = LogManager.getLogger(TailsPlatform.MOD_ID);

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