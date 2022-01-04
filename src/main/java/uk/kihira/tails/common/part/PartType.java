/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import javax.annotation.Nullable;

/**
 * Different types of parts.
 */
public enum PartType {
	TAIL("tail"),
	EARS("ears"),
	WINGS("wings"),
	MUZZLE("muzzle"),
	HEAD_ACCESSORY("head_accessory");

	private final String id;

	private PartType(String id) {
		this.id = id;
	}

	/**
	 * @return The {@link PartType}'s id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the {@link PartType} with the given id.
	 * @param id
	 * @return The part type, or {@code null} if the given id does not match any {@link PartType} ids.
	 */
	@Nullable
	public static PartType forId(String id) {
		for (PartType type : values())
			if (type.id.equals(id))
				return type;

		return null;
	}
}