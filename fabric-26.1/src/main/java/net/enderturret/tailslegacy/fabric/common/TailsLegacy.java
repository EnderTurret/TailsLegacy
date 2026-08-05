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

import net.fabricmc.api.ModInitializer;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.fabric.common.network.TailsNetworkManager;

public final class TailsLegacy implements ModInitializer {

	@Internal
	public static final Logger LOGGER = LogManager.getLogger(TailsPlatform.MOD_ID);

	@Internal
	public TailsLegacy() {}

	@Override
	public void onInitialize() {
		ServerEventHandler.register();
		TailsNetworkManager.registerPackets();
	}
}