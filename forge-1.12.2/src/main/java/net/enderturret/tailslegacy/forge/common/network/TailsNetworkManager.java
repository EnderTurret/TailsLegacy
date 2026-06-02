/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.network;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import net.enderturret.tailslegacy.common.TailsPlatform;

/**
 * Manages the Tails network stuff, like the channel and packet registration.
 * @author EnderTurret
 */
@Internal
public class TailsNetworkManager {

	private static final String VERSION = "1";

	private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(TailsPlatform.MOD_ID);

	static {
		CHANNEL.registerMessage(new C2SPlayerDataMessage.Handler(), C2SPlayerDataMessage.class, 0, Side.SERVER);
		CHANNEL.registerMessage(new S2CPlayerDataMessage.Handler(), S2CPlayerDataMessage.class, 1, Side.CLIENT);
		CHANNEL.registerMessage(new PlayerDataMapMessage.Handler(), PlayerDataMapMessage.class, 2, Side.CLIENT);
	}

	public static SimpleNetworkWrapper get() {
		return CHANNEL;
	}
}