/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client;

import com.google.gson.Gson;

import uk.kihira.tails.common.client.part.LocalPartManager;

public final class TailsClientInternal {

	static TailsClientPlatform platform;

	public static Gson getClientGson() {
		return LocalPartManager.GSON;
	}
}