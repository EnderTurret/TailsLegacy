/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;

import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;
import net.enderturret.tailslegacy.fabric.common.network.PlayerDataMapMessage;

/**
 * A server event handler, for handling events on the server.
 */
@Internal
public final class ServerEventHandler {

	public static void register() {
		ServerPlayerEvents.JOIN.register(ServerEventHandler::onPlayerLogin);
		ServerPlayerEvents.LEAVE.register(ServerEventHandler::onPlayerLogout);
	}

	static void onPlayerLogin(ServerPlayer player) {
		// Send current known tails to uk.kihira.tails.client
		if (ServerPlayNetworking.canSend(player, PlayerDataMapMessage.TYPE))
			ServerPlayNetworking.send(player, new PlayerDataMapMessage(ServerPlayerPartManager.get().getData()));
	}

	static void onPlayerLogout(ServerPlayer player) {
		// Server doesn't save tails so we discard.
		ServerPlayerPartManager.get().remove(player.getUUID());
	}
}