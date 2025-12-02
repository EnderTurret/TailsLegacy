/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.proxy;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;

import uk.kihira.tails.common2.api.IPlayerPartManager;

/**
 * A proxy interface.
 * If your mod doesn't have one, is it really a mod?
 * @author EnderTurret
 */
@Internal
public interface IProxy {

	/**
	 * Uses the power of <em>quiet class references</em> <sup>(reflection)</sup> to create and return a {@code ClientProxy}.
	 * Please handle with care. <sup>(Read: enclose within 5,000 Suppliers and place calling code deep inside a forgotten package.)</sup>
	 * @return A {@code ClientProxy}, made with a sprinkle of <strike>love</strike> {@code Dist.CLIENT}.
	 */
	@Internal
	public static IProxy makeClientProxy() {
		try {
			return (IProxy) Class.forName("uk.kihira.tails.neoforge.proxy.client.ClientProxy").getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @return The part manager.
	 */
	public IPlayerPartManager getPartManager();

	/**
	 * @return The sided {@link Gson} instance.
	 */
	public Gson getSidedGson();
}