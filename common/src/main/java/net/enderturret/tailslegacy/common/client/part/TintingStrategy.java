/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Locale;

import org.jetbrains.annotations.Nullable;

/**
 * Represents different strategies for tinting textures.
 * @author EnderTurret
 */
public enum TintingStrategy {

	/**
	 * Apply all three tints to the texture.
	 */
	TRIPLE_TINT,
	/**
	 * Apply only the first tint to the texture.
	 */
	SINGLE_TINT,
	/**
	 * Perform no tinting; leave the texture unchanged.
	 */
	NO_TINT;

	/**
	 * The id of the tinting strategy.
	 */
	public final String id = name().toLowerCase(Locale.ENGLISH);

	/**
	 * Returns the tinting strategy with the given id, or {@code null} if one doesn't exist.
	 * @param id The id of the desired tinting strategy.
	 * @return The tinting strategy.
	 */
	@Nullable
	public static TintingStrategy of(String id) {
		for (TintingStrategy strat : values())
			if (strat.id.equals(id))
				return strat;

		return null;
	}
}