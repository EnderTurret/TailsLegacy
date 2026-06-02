/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common;

import java.util.Locale;

public enum TailsDirection {

    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST;

	public final String name = name().toLowerCase(Locale.ENGLISH);

	public static TailsDirection byName(String name) {
		for (TailsDirection direction : values())
			if (direction.name.equals(name))
				return direction;

		throw new IllegalArgumentException("Unknown direction: " + name);
	}
}