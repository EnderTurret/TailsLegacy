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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.proxy.IProxy;
import uk.kihira.tails.common.proxy.ServerProxy;
import uk.kihira.tails.forge.common.network.TailsNetworkManager;

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
	public Tails() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
		TailsNetworkManager.get();
	}
}