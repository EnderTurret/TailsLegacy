/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common.network;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Manages the Tails network stuff, like the channel and packet registration.
 * @author EnderTurret
 */
@Internal
public class TailsNetworkManager {

	public static void registerPackets() {
		PayloadTypeRegistry.serverboundPlay().register(C2SPlayerDataMessage.TYPE, C2SPlayerDataMessage.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(S2CPlayerDataMessage.TYPE, S2CPlayerDataMessage.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PlayerDataMapMessage.TYPE, PlayerDataMapMessage.STREAM_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(C2SPlayerDataMessage.TYPE, C2SPlayerDataMessage::handle);
	}
}