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
import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkDirection;

import uk.kihira.tails.api.ITailsSyncService;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.part.PartsData;

/**
 * A common proxy for common things.<br>
 * This definitely doesn't completely defeat the purpose of proxies. I don't know where you got that idea.<sup>/s</sup>
 */
public class CommonProxy {

	public static ITailsSyncService sync;

	protected final Map<UUID, PartsData> partsData = new HashMap<>();

	/**
	 * Uses the power of <em>quiet class references</em> <sup>(reflection)</sup> to create and return a ClientProxy.<br>
	 * Please handle with care. <sup>(Read: enclose within 5,000 Suppliers and place calling code deep inside a forgotten package.)</sup>
	 * @return A ClientProxy, made with a sprinkle of <strike>love</strike> Dist.CLIENT.
	 */
	public static CommonProxy makeClientProxy() {
		try {
			return (CommonProxy) Class.forName("uk.kihira.tails.proxy.client.ClientProxy").getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * A generic initialization method.
	 */
	public void init() {
		registerMessages();
	}

	/**
	 * A generic message registration method.
	 */
	protected void registerMessages() {
		Tails.CHANNEL.registerMessage(0, PlayerDataMessage.class, PlayerDataMessage::encode, PlayerDataMessage::decode, PlayerDataMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		Tails.CHANNEL.registerMessage(1, PlayerDataMapMessage.class, PlayerDataMapMessage::encode, PlayerDataMapMessage::decode, PlayerDataMapMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	/**
	 * Adds the given part data for the given {@link UUID}.
	 * @param uuid The {@link UUID} that the part data is for.
	 * @param partsData The part data.
	 */
	public void addPartsData(UUID uuid, PartsData partsData) {
		if (uuid != null)
			this.partsData.put(uuid, partsData);
	}

	/**
	 * Removes all part data for the given {@link UUID}.
	 * @param uuid The {@link UUID} to remove all part data for.
	 */
	public void removePartsData(UUID uuid) {
		if (hasPartsData(uuid))
			partsData.remove(uuid);
	}

	/**
	 * Removes all part data.
	 */
	public void clearAllPartsData() {
		partsData.clear();
	}

	/**
	 * Whether the given {@link UUID} has any part data.
	 * @param uuid The {@link UUID} to check for part data for.
	 * @return True if the given {@link UUID} has any part data.
	 */
	public boolean hasPartsData(UUID uuid) {
		return uuid != null && partsData.containsKey(uuid) && !partsData.get(uuid).isEmpty();
	}

	/**
	 * Returns the part data for the given {@link UUID}.<br>
	 * Returns {@link PartsData#EMPTY} if there is no part data present for it.
	 * @param uuid The {@link UUID} to retrieve part data for.
	 * @return The part data.
	 */
	public PartsData getPartsData(UUID uuid) {
		return partsData.computeIfAbsent(uuid, k -> sync != null ? sync.query(k) : PartsData.EMPTY);
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
		throw new UnsupportedOperationException("No tails library available on server");
	}

	public void deleteTexture(ResourceLocation tex) {}

	public Gson getSidedGson() {
		return Tails.SERVER_GSON;
	}
}