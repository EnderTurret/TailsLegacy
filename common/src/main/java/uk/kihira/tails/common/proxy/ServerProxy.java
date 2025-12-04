/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.proxy;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;

import uk.kihira.tails.common.api.IPlayerPartManager;
import uk.kihira.tails.common.gson.TailsGsonHelper;
import uk.kihira.tails.common.part.PlayerPartManager;

/**
 * It's a server proxy alright.
 */
@Internal
public final class ServerProxy implements IProxy {

	private final IPlayerPartManager partManager = new PlayerPartManager();

	@Override
	public IPlayerPartManager getPartManager() {
		return partManager;
	}

	@Override
	public Gson getSidedGson() {
		return TailsGsonHelper.SERVER_GSON;
	}
}