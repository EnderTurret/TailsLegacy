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

import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;
import net.enderturret.tailslegacy.forge.common.network.PlayerDataMapMessage;
import net.enderturret.tailslegacy.forge.common.network.TailsNetworkManager;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
@EventBusSubscriber(modid = TailsPlatform.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public final class ServerEventHandler {

	@SubscribeEvent
	static void onPlayerLogin(PlayerLoggedInEvent event) {
		final ServerPlayer player = (ServerPlayer) event.getEntity();
		// Send current known tails to uk.kihira.tails.client
		TailsNetworkManager.get().send(PacketDistributor.PLAYER.with(() -> player), new PlayerDataMapMessage(ServerPlayerPartManager.get().getData()));
	}

	@SubscribeEvent
	static void onPlayerLogout(PlayerLoggedOutEvent event) {
		// Server doesn't save tails so we discard.
		ServerPlayerPartManager.get().remove(event.getEntity().getUUID());
	}
}