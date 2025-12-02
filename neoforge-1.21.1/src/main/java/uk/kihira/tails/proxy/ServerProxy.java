/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;

import uk.kihira.tails.common2.LibraryManager;
import uk.kihira.tails.common2.api.IPlayerPartManager;
import uk.kihira.tails.common2.gson.TailsGsonHelper;
import uk.kihira.tails.common2.part.PlayerPartManager;

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
		return TailsGsonHelper.SERVER_GSON;
	}
}