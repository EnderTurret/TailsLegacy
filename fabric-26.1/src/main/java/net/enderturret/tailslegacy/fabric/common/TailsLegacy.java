/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;

@Mod(TailsPlatform.MOD_ID)
public final class TailsLegacy {

	@Internal
	public static final Logger LOGGER = LogManager.getLogger(TailsPlatform.MOD_ID);

	@Internal
	public static String migratingData;

	@Internal
	public TailsLegacy(ModContainer mc) {
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			migratingData = TailsInternal.maybeMigrateTomlConfig(FMLPaths.CONFIGDIR.get());
			mc.registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
		}
	}
}