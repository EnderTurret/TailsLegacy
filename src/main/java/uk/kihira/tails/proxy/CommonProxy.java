/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.ServerEventHandler;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryEntriesMessage;
import uk.kihira.tails.common.network.LibraryRequestMessage;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;
import uk.kihira.tails.common.part.PartsData;

/**
 * A common proxy for common things.<br>
 * This definitely doesn't completely defeat the purpose of proxies. I don't know where you got that idea.<sup>/s</sup>
 */
public class CommonProxy {

	protected final Map<UUID, PartsData> partsData = new HashMap<>();
	protected LibraryManager libraryManager;

	/**
	 * Uses the power of <em>quiet class references</em> <sup>(reflection)</sup> to create and return a ClientProxy.<br>
	 * Please handle with care. <sup>(Read: enclose within 5,000 Suppliers and place calling code deep inside a forgotten package.)</sup>
	 * @return A ClientProxy, made with a sprinkle of <strike>love</strike> Dist.CLIENT.
	 */
	public static CommonProxy makeClientProxy() {
		try {
			return (CommonProxy) Class.forName("uk.kihira.tails.proxy.client.ClientProxy").newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * A generic initialization method.
	 */
	public void init() {
		registerMessages();
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
		libraryManager = new LibraryManager();
	}

	/**
	 * A generic message registration method.
	 */
	protected void registerMessages() {
		Tails.CHANNEL.registerMessage(0, PlayerDataMessage.class, PlayerDataMessage::encode, PlayerDataMessage::decode, PlayerDataMessage::handle);
		Tails.CHANNEL.registerMessage(1, PlayerDataMapMessage.class, PlayerDataMapMessage::encode, PlayerDataMapMessage::decode, PlayerDataMapMessage::handle);
		Tails.CHANNEL.registerMessage(2, LibraryEntriesMessage.class, LibraryEntriesMessage::encode, LibraryEntriesMessage::decode, LibraryEntriesMessage::handle);
		Tails.CHANNEL.registerMessage(3, LibraryRequestMessage.class, LibraryRequestMessage::encode, LibraryRequestMessage::decode, LibraryRequestMessage::handle);
		Tails.CHANNEL.registerMessage(4, ServerCapabilitiesMessage.class, ServerCapabilitiesMessage::encode, ServerCapabilitiesMessage::decode, ServerCapabilitiesMessage::handle);
	}

	/**
	 * A generic renderer registration method.
	 */
	public void registerRenderers() {}

	/**
	 * Adds the given part data for the given {@link UUID}.
	 * @param uuid The {@link UUID} that the part data is for.
	 * @param partsData The part data.
	 */
	public void addPartsData(UUID uuid, PartsData partsData) {
		if (uuid != null)
			this.partsData.put(uuid, partsData);
			//Tails.LOGGER.debug("Added part data for {}: {}", uuid.toString(), partsData);
		//else Tails.LOGGER.warn("Attempted to add part data with null UUID! {}", partsData);
	}

	/**
	 * Removes all part data for the given {@link UUID}.
	 * @param uuid The {@link UUID} to remove all part data for.
	 */
	public void removePartsData(UUID uuid) {
		if (hasPartsData(uuid)) {
			if (EffectiveSide.get() == LogicalSide.SERVER) {
				// TODO Tell uk.kihira.tails.client to remove textures.
				//Tails.networkWrapper.sendToAll(new PlayerDataMessage(uuid, this.partsData.get(uuid), true));
			}
			partsData.remove(uuid);
			//Tails.LOGGER.debug("Removed part data for {}", uuid.toString());
		}
	}

	/**
	 * Removes all part data.
	 */
	public void clearAllPartsData() {
		partsData.clear();
		//Tails.LOGGER.debug("Clearing parts data");
	}

	/**
	 * Whether the given {@link UUID} has any part data.
	 * @param uuid The {@link UUID} to check for part data for.
	 * @return True if the given {@link UUID} has any part data.
	 */
	public boolean hasPartsData(UUID uuid) {
		return uuid != null && partsData.containsKey(uuid);
	}

	/**
	 * Returns the part data for the given {@link UUID}.<br>
	 * Returns {@code null} if there is no part data present for it.
	 * @param uuid The {@link UUID} to retrieve part data for.
	 * @return The part data.
	 */
	@Nullable
	public PartsData getPartsData(UUID uuid) {
		return partsData.get(uuid);
	}

	/**
	 * @return The part data map.
	 */
	public Map<UUID, PartsData> getPartsData() {
		return partsData;
	}

	/**
	 * @return The library manager.
	 */
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}