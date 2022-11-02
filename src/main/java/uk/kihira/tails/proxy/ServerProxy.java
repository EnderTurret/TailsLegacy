/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;

import uk.kihira.tails.api.IPlayerPartManager;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PlayerPartManager;

/**
 * It's a server proxy alright.
 */
@Internal
public class ServerProxy implements IProxy {

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