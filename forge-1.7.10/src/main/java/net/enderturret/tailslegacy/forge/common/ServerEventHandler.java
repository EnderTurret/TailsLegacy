/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.entity.player.EntityPlayerMP;

import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;
import net.enderturret.tailslegacy.forge.common.network.S2CBulkPlayerDataMessage;
import net.enderturret.tailslegacy.forge.common.network.TailsNetworkManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
public final class ServerEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		final EntityPlayerMP player = (EntityPlayerMP) event.player;
		// Send current known tails to uk.kihira.tails.client
		TailsNetworkManager.get().sendTo(new S2CBulkPlayerDataMessage(ServerPlayerPartManager.get().getData()), player);
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		ServerPlayerPartManager.get().remove(event.player.getUniqueID());
	}
}