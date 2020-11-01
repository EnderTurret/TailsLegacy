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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.ServerEventHandler;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryEntriesMessage;
import uk.kihira.tails.common.network.LibraryRequestMessage;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;

public class CommonProxy {

	protected final HashMap<UUID, PartsData> partsData = new HashMap<>();
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

	public void init() {
		registerMessages();
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
		libraryManager = new LibraryManager();
	}

	protected void registerMessages() {
		Tails.CHANNEL.registerMessage(0, PlayerDataMessage.class, PlayerDataMessage::encode, PlayerDataMessage::decode, PlayerDataMessage::handle);
		Tails.CHANNEL.registerMessage(1, PlayerDataMapMessage.class, PlayerDataMapMessage::encode, PlayerDataMapMessage::decode, PlayerDataMapMessage::handle);
		Tails.CHANNEL.registerMessage(2, LibraryEntriesMessage.class, LibraryEntriesMessage::encode, LibraryEntriesMessage::decode, LibraryEntriesMessage::handle);
		Tails.CHANNEL.registerMessage(3, LibraryRequestMessage.class, LibraryRequestMessage::encode, LibraryRequestMessage::decode, LibraryRequestMessage::handle);
		Tails.CHANNEL.registerMessage(4, ServerCapabilitiesMessage.class, ServerCapabilitiesMessage::encode, ServerCapabilitiesMessage::decode, ServerCapabilitiesMessage::handle);
	}

	public void registerRenderers() {}

	public void addPartsData(UUID uuid, PartsData partsData) {
		if (uuid != null) {
			this.partsData.put(uuid, partsData);
			//Tails.LOGGER.debug("Added part data for {}: {}", uuid.toString(), partsData);
		}
		//else Tails.LOGGER.warn("Attempted to add part data with null UUID! {}", partsData);
	}

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

	public void clearAllPartsData() {
		partsData.clear();
		//Tails.LOGGER.debug("Clearing parts data");
	}

	public boolean hasPartsData(UUID uuid) {
		return uuid != null && partsData.containsKey(uuid);
	}

	public PartsData getPartsData(UUID uuid) {
		return partsData.get(uuid);
	}

	public Map<UUID, PartsData> getPartsData() {
		return partsData;
	}

	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}
