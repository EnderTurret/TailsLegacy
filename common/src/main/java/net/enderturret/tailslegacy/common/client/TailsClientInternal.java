/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

import net.enderturret.tailslegacy.common.client.part.LocalPartManager;

public final class TailsClientInternal {

	static TailsClientPlatform platform;
	static ExecutorService executor;

	static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);

	public static Gson getClientGson() {
		return LocalPartManager.GSON;
	}
}