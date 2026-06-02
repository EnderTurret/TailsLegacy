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

import net.minecraftforge.common.MinecraftForge;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.forge.client.ClientEventHandler;
import net.enderturret.tailslegacy.forge.client.toast.ToastManager;
import net.enderturret.tailslegacy.forge.common.network.TailsNetworkManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = TailsPlatform.MOD_ID, name = "Tails Legacy", acceptedMinecraftVersions = "[1.7.10,1.8)", acceptableRemoteVersions = "*")
public final class Tails {

	@Internal
	public static final Logger LOGGER = LogManager.getLogger(TailsPlatform.MOD_ID);

	@Internal
	public Tails() {
		TailsNetworkManager.get();

		FMLCommonHandler.instance().bus().register(new ServerEventHandler());

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
			FMLCommonHandler.instance().bus().register(new ClientEventHandler.CommonHandlerBus());
			FMLCommonHandler.instance().bus().register(ToastManager.INSTANCE);
		}
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