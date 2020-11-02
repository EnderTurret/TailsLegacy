/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import javax.annotation.Nullable;

//NOTE: We rely on the order of this, don't re-arrange, only append! Order is for legacy reasons.
public enum PartType {
	TAIL("tail"),
	EARS("ears"),
	WINGS("wings"),
	MUZZLE("muzzle");

	private final String id;

	private PartType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Nullable
	public static PartType forId(String id) {
		for (PartType type : values())
			if (type.id.equals(id))
				return type;

		return null;
	}
}