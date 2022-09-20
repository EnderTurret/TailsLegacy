/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.common.Tails;

@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler {

	@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	static class Forge {

		private static boolean sentPartInfoToServer = false;
		private static boolean clearAllPartInfo = false;

		/*
		 * Tails Editor Button
		 */
		@SubscribeEvent
		static void onScreenInitPost(ScreenEvent.Init.Post event) {
			if (event.getScreen() instanceof PauseScreen)
				event.addListener(new Button(event.getScreen().width / 2 - 35, event.getScreen().height - 25, 70, 20, Component.translatable("tails.gui.button.editor"), b -> {
					Minecraft.getInstance().setScreen(EditorScreen.openDefault());
				}));
		}

		/*
		 * Tails Syncing
		 */
		@SubscribeEvent
		static void onConnectToServer(ClientPlayerNetworkEvent.LoggingIn event) {
			// Add local player texture to map.
			if (LocalPartManager.getLocalPartsData() != null)
				Tails.PROXY.getPartManager().set(ClientUtils.getPlayerUUID(), LocalPartManager.getLocalPartsData());
		}

		@SubscribeEvent
		static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}

		@SubscribeEvent
		static void onClientTick(TickEvent.ClientTickEvent e) {
			if (e.phase == TickEvent.Phase.START)
				if (clearAllPartInfo) {
					Tails.PROXY.getPartManager().clear();
					clearAllPartInfo = false;
				}
				// World can't be null if we want to send a packet it seems.
				else if (!sentPartInfoToServer && Minecraft.getInstance().level != null) {
					LocalPartManager.syncToServer();

					sentPartInfoToServer = true;
				}
		}
	}
}
