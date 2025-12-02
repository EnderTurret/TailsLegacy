package uk.kihira.tails.common;

import org.jetbrains.annotations.ApiStatus.Internal;

public final class TailsInternal {

	static TailsPlatform platform;

	/**
	 * Whether to enable network debugging features, such as printing received packet data to the log.
	 */
	@Internal
	public static final boolean DEBUG_NETWORK = Boolean.getBoolean("tails.debugNetwork");
}