/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.forge.common.network.TailsNetworkManager;

@Mod(TailsPlatform.MOD_ID)
public final class Tails {

	@Internal
	public static final Logger LOGGER = LogManager.getLogger(TailsPlatform.MOD_ID);

	@Internal
	public static String migratingData;

	@Internal
	public Tails() {
		TailsNetworkManager.get();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			migratingData = TailsInternal.maybeMigrateTomlConfig(FMLPaths.CONFIGDIR.get());
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
		}
	}
}