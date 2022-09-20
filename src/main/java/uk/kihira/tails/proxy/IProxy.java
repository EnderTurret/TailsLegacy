/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import com.google.gson.Gson;

import uk.kihira.tails.api.IPlayerPartManager;
import uk.kihira.tails.common.LibraryManager;

/**
 * A proxy interface.
 * If your mod doesn't have one, is it really a mod?
 * @author EnderTurret
 */
public interface IProxy {

	/**
	 * Uses the power of <em>quiet class references</em> <sup>(reflection)</sup> to create and return a ClientProxy.<br>
	 * Please handle with care. <sup>(Read: enclose within 5,000 Suppliers and place calling code deep inside a forgotten package.)</sup>
	 * @return A ClientProxy, made with a sprinkle of <strike>love</strike> Dist.CLIENT.
	 */
	public static IProxy makeClientProxy() {
		try {
			return (IProxy) Class.forName("uk.kihira.tails.proxy.client.ClientProxy").getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @return The library manager.
	 */
	public LibraryManager getLibraryManager();

	public IPlayerPartManager getPartManager();

	public Gson getSidedGson();
}