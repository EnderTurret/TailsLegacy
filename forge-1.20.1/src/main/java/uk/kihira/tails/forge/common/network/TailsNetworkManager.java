/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common.network;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;

import uk.kihira.tails.common.TailsPlatform;

/**
 * Manages the Tails network stuff, like the channel and packet registration.
 * @author EnderTurret
 */
@Internal
@EventBusSubscriber(modid = TailsPlatform.MOD_ID)
public class TailsNetworkManager {

	@SubscribeEvent
	static void registerPackets(RegisterPayloadHandlersEvent e) {
		e.registrar("1").executesOn(HandlerThread.NETWORK).optional()
		.playToServer(C2SPlayerDataMessage.TYPE, C2SPlayerDataMessage.STREAM_CODEC, C2SPlayerDataMessage::handle)
		.playToClient(S2CPlayerDataMessage.TYPE, S2CPlayerDataMessage.STREAM_CODEC, S2CPlayerDataMessage::handle)
		.playToClient(PlayerDataMapMessage.TYPE, PlayerDataMapMessage.STREAM_CODEC, PlayerDataMapMessage::handle);
	}
}