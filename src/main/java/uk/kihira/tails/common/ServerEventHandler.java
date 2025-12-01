/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import uk.kihira.tails.common.network.PlayerDataMapMessage;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
@EventBusSubscriber(modid = Tails.MOD_ID)
public final class ServerEventHandler {

	@SubscribeEvent
	static void onPlayerLogin(PlayerLoggedInEvent event) {
		final ServerPlayer player = (ServerPlayer) event.getEntity();
		// Send current known tails to uk.kihira.tails.client
		PacketDistributor.sendToPlayer(player, new PlayerDataMapMessage(Tails.PROXY.getPartManager().getData()));
	}

	@SubscribeEvent
	static void onPlayerLogout(PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		Tails.PROXY.getPartManager().remove(event.getEntity().getUUID());
	}
}