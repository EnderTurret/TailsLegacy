/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.ServerEventHandler;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CommonProxy {

	protected final HashMap<UUID, PartsData> partsData = new HashMap<>();
	protected LibraryManager libraryManager;

	public void init() {
		registerMessages();
		registerHandlers();
		libraryManager = new LibraryManager();
	}

	protected void registerMessages() {
		Tails.networkWrapper.registerMessage(0, PlayerDataMessage.class, PlayerDataMessage::toBytes, PlayerDataMessage::fromBytes, PlayerDataMessage::onMessage);
		Tails.networkWrapper.registerMessage(1, PlayerDataMapMessage.class, PlayerDataMapMessage::toBytes, PlayerDataMapMessage::fromBytes, PlayerDataMapMessage::onMessage);
		Tails.networkWrapper.registerMessage(2, LibraryEntriesMessage.class, LibraryEntriesMessage::toBytes, LibraryEntriesMessage::fromBytes, LibraryEntriesMessage::onMessage);
		Tails.networkWrapper.registerMessage(3, LibraryRequestMessage.class, LibraryRequestMessage::toBytes, LibraryRequestMessage::fromBytes, LibraryRequestMessage::onMessage);
		Tails.networkWrapper.registerMessage(4, ServerCapabilitiesMessage.class, ServerCapabilitiesMessage::toBytes, ServerCapabilitiesMessage::fromBytes, ServerCapabilitiesMessage::onMessage);
	}

	protected void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}

	public void registerRenderers() {}

	public void addPartsData(UUID uuid, PartsData partsData) {
		if (uuid != null) {
			this.partsData.put(uuid, partsData);
			Tails.logger.debug(String.format("Added part data for %s: %s", uuid.toString(), partsData));
		}
		else Tails.logger.warn(String.format("Attempted to add part data with null UUID! %s", partsData));
	}

	public void removePartsData(UUID uuid) {
		if (hasPartsData(uuid)) {
			if (EffectiveSide.get() == LogicalSide.SERVER) {
				//todo Tell uk.kihira.tails.client to remove textures
				//Tails.networkWrapper.sendToAll(new PlayerDataMessage(uuid, this.partsData.get(uuid), true));
			}
			this.partsData.remove(uuid);
			Tails.logger.debug(String.format("Removed part data for %s", uuid.toString()));
		}
	}

	public void clearAllPartsData() {
		this.partsData.clear();
		Tails.logger.debug("Clearing parts data");
	}

	public boolean hasPartsData(UUID uuid) {
		return uuid != null && this.partsData.containsKey(uuid);
	}

	public PartsData getPartsData(UUID uuid) {
		return this.partsData.get(uuid);
	}

	public Map<UUID, PartsData> getPartsData() {
		return this.partsData;
	}

	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}
