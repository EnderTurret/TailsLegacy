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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import uk.kihira.tails.common.part.ServerPlayerPartManager;
import uk.kihira.tails.forge.common.network.PlayerDataMapMessage;
import uk.kihira.tails.forge.common.network.TailsNetworkManager;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
public final class ServerEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		final EntityPlayerMP player = (EntityPlayerMP) event.player;
		// Send current known tails to uk.kihira.tails.client
		TailsNetworkManager.get().sendTo(new PlayerDataMapMessage(ServerPlayerPartManager.get().getData()), player);
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		ServerPlayerPartManager.get().remove(event.player.getUniqueID());
	}
}