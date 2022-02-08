/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import uk.kihira.tails.common.network.PlayerDataMapMessage;

/**
 * A server event handler, for handling events on the server.
 */
public class ServerEventHandler {

	@SubscribeEvent
	void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		final ServerPlayer player = (ServerPlayer) event.getPlayer();
		// Send current known tails to uk.kihira.tails.client
		Tails.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerDataMapMessage(Tails.PROXY.getPartsData()));
	}

	@SubscribeEvent
	void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		Tails.PROXY.removePartsData(Player.createPlayerUUID(event.getPlayer().getGameProfile()));
	}
}