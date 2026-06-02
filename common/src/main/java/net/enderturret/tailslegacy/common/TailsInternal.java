/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common;

import org.jetbrains.annotations.ApiStatus.Internal;

public final class TailsInternal {

	static TailsPlatform platform;

	/**
	 * Whether to enable network debugging features, such as printing received packet data to the log.
	 */
	@Internal
	public static final boolean DEBUG_NETWORK = Boolean.getBoolean("tailslegacy.debugNetwork");
}