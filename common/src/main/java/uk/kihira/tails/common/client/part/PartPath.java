/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.part;

import uk.kihira.tails.common.client.duck.TailsModelPart;

public final class PartPath {

	private final String[] path;

	public PartPath(String[] path) {
		this.path = path;
	}

	public PartPath(String path) {
		this(path.split("\\."));
	}

	public TailsModelPart traverse(TailsModelPart root) {
		for (String segment : path)
			root = root.t$getChild(segment);

		return root;
	}
}