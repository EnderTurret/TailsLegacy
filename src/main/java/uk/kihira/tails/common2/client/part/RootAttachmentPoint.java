/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.part;

import java.util.NavigableSet;

/**
 * Represents a "root attachment point," which can be thought of as the general body part of {@link AttachmentPoint AttachmentPoints}.
 * @param id The id of the {@code RootAttachmentPoint}.
 * @see AttachmentPoint
 * @see AttachmentPoints
 * @author EnderTurret
 */
public record RootAttachmentPoint(String id) implements Comparable<RootAttachmentPoint> {

	public String translationKey() {
		return "tails.attachment." + id;
	}

	/**
	 * This is a convenience method for {@link AttachmentPoints#getAll(RootAttachmentPoint)}.
	 * @return The children of this {@code RootAttachmentPoint}.
	 */
	public NavigableSet<AttachmentPoint> children() {
		return AttachmentPoints.getAll(this);
	}

	@Override
	public int compareTo(RootAttachmentPoint o) {
		return o == null ? 1 : id.compareTo(o.id);
	}

	@Override
	public String toString() {
		return id;
	}
}