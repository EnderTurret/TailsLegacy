/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import uk.kihira.tails.common.network.C2SPlayerDataMessage;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.S2CPlayerDataMessage;

/**
 * Manages the Tails network stuff, like the channel and packet registration.
 * @author EnderTurret
 */
@Internal
public class TailsNetworkManager {

	@Internal
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Tails.MOD_ID, "channel"), () -> "™", v -> true, v -> true);

	/**
	 * Whether to enable network debugging features, such as printing received packet data to the log.
	 */
	@Internal
	public static final boolean DEBUG_NETWORK = Boolean.getBoolean("tails.debugNetwork");

	@Internal
	public static void registerMessages() {
		CHANNEL.registerMessage(0, C2SPlayerDataMessage.class, C2SPlayerDataMessage::encode, C2SPlayerDataMessage::decode, C2SPlayerDataMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(1, S2CPlayerDataMessage.class, S2CPlayerDataMessage::encode, S2CPlayerDataMessage::decode, S2CPlayerDataMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(2, PlayerDataMapMessage.class, PlayerDataMapMessage::encode, PlayerDataMapMessage::decode, PlayerDataMapMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
}