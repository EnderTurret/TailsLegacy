/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.forge.common.network.PlayerDataMapMessage;
import uk.kihira.tails.forge.common.network.TailsNetworkManager;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
@EventBusSubscriber(modid = TailsPlatform.MOD_ID)
public final class ServerEventHandler {

	@SubscribeEvent
	static void onPlayerLogin(PlayerLoggedInEvent event) {
		final EntityPlayerMP player = (EntityPlayerMP) event.player;
		// Send current known tails to uk.kihira.tails.client
		TailsNetworkManager.get().sendTo(new PlayerDataMapMessage(Tails.PROXY.getPartManager().getData()), player);
	}

	@SubscribeEvent
	static void onPlayerLogout(PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		Tails.PROXY.getPartManager().remove(event.player.getUniqueID());
	}
}