/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

import uk.kihira.tails.common.network.PlayerDataMapMessage;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public final class ServerEventHandler {

	@SubscribeEvent
	static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		final ServerPlayer player = (ServerPlayer) event.getEntity();
		// Send current known tails to uk.kihira.tails.client
		TailsNetworkManager.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerDataMapMessage(Tails.PROXY.getPartManager().getData()));
	}

	@SubscribeEvent
	static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		Tails.PROXY.getPartManager().remove(UUIDUtil.getOrCreatePlayerUUID(event.getEntity().getGameProfile()));
	}
}