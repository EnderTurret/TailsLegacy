/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;
import net.enderturret.tailslegacy.neoforge.common.network.PlayerDataMapMessage;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
@EventBusSubscriber(modid = TailsPlatform.MOD_ID)
public final class ServerEventHandler {

	@SubscribeEvent
	static void onPlayerLogin(PlayerLoggedInEvent event) {
		final ServerPlayer player = (ServerPlayer) event.getEntity();
		// Send current known tails to uk.kihira.tails.client
		PacketDistributor.sendToPlayer(player, new PlayerDataMapMessage(ServerPlayerPartManager.get().getData()));
	}

	@SubscribeEvent
	static void onPlayerLogout(PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		ServerPlayerPartManager.get().remove(event.getEntity().getUUID());
	}
}