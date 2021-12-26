/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;

/**
 * A server event handler, for handling events on the server.
 */
public class ServerEventHandler {

	@SubscribeEvent
	void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		final ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		// Send current known tails to uk.kihira.tails.client
		Tails.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerDataMapMessage(Tails.PROXY.getPartsData()));
		Tails.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerCapabilitiesMessage(Tails.libraryEnabled));
	}

	@SubscribeEvent
	void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		Tails.PROXY.removePartsData(PlayerEntity.createPlayerUUID(event.getPlayer().getGameProfile()));
	}
}