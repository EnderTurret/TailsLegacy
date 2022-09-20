/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import com.google.gson.Gson;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.api.IPlayerPartManager;
import uk.kihira.tails.api.ITailsSyncService;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PlayerPartManager;

/**
 * A common proxy for common things.<br>
 * This definitely doesn't completely defeat the purpose of proxies. I don't know where you got that idea.<sup>/s</sup>
 */
public class CommonProxy implements IProxy {

	public static ITailsSyncService sync;

	private final IPlayerPartManager partManager = new PlayerPartManager();

	@Override
	public LibraryManager getLibraryManager() {
		throw new UnsupportedOperationException("No tails library available on server");
	}

	@Override
	public IPlayerPartManager getPartManager() {
		return partManager;
	}

	@Override
	public Gson getSidedGson() {
		return Tails.SERVER_GSON;
	}
}