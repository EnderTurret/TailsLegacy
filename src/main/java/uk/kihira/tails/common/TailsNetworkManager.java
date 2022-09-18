package uk.kihira.tails.common;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TailsNetworkManager {

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Tails.MOD_ID, "channel"), () -> "™", v -> true, v -> true);

	/**
	 * Whether to enable network debugging features, such as printing received packet data to the log.
	 */
	public static final boolean DEBUG_NETWORK = Boolean.getBoolean("tails.debugNetwork");
}