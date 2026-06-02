/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.network;

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import net.enderturret.tailslegacy.common.TailsPlatform;

/**
 * Manages the Tails network stuff, like the channel and packet registration.
 * @author EnderTurret
 */
@Internal
public class TailsNetworkManager {

	private static final String VERSION = "1";

	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			ResourceLocation.fromNamespaceAndPath(TailsPlatform.MOD_ID, "sync"), () -> VERSION,
			NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));

	static {
		CHANNEL.registerMessage(0, C2SPlayerDataMessage.class, C2SPlayerDataMessage::encode, C2SPlayerDataMessage::decode, C2SPlayerDataMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(1, S2CPlayerDataMessage.class, S2CPlayerDataMessage::encode, S2CPlayerDataMessage::decode, S2CPlayerDataMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(2, PlayerDataMapMessage.class, PlayerDataMapMessage::encode, PlayerDataMapMessage::decode, PlayerDataMapMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static SimpleChannel get() {
		return CHANNEL;
	}
}