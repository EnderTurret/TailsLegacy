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

import com.google.gson.Gson;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.api.IPlayerPartManager;
import uk.kihira.tails.api.ITailsSyncService;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.PlayerPartManager;

/**
 * A common proxy for common things.<br>
 * This definitely doesn't completely defeat the purpose of proxies. I don't know where you got that idea.<sup>/s</sup>
 */
public class CommonProxy {

	public static ITailsSyncService sync;

	protected IPlayerPartManager partManager;

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
		partManager = new PlayerPartManager();
	}

	/**
	 * @return The library manager.
	 */
	public LibraryManager getLibraryManager() {
		throw new UnsupportedOperationException("No tails library available on server");
	}

	public IPlayerPartManager getPartManager() {
		return partManager;
	}

	public void deleteTexture(ResourceLocation tex) {}

	public Gson getSidedGson() {
		return Tails.SERVER_GSON;
	}
}