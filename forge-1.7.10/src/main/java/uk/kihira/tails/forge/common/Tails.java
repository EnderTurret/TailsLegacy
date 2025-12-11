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

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.forge.client.ClientEventHandler;
import uk.kihira.tails.forge.common.network.TailsNetworkManager;

@Mod(modid = TailsPlatform.MOD_ID, name = "Tails Legacy", acceptedMinecraftVersions = "[1.12,1.13)", acceptableRemoteVersions = "*")
public final class Tails {

	@Internal
	public static final Logger LOGGER = LogManager.getLogger(TailsPlatform.MOD_ID);

	@Internal
	public Tails() {
		TailsNetworkManager.get();
	}

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			ClientEventHandler.onPreInit(e);
	}

	@EventHandler
	public void onInit(FMLInitializationEvent e) {

	}

	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			ClientEventHandler.onPostInit();
	}
}