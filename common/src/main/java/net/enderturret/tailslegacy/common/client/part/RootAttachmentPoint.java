/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.NavigableSet;
import java.util.Objects;

/**
 * Represents a "root attachment point," which can be thought of as the general body part of {@link AttachmentPoint AttachmentPoints}.
 * @see AttachmentPoint
 * @see AttachmentPoints
 * @author EnderTurret
 */
public final class RootAttachmentPoint implements Comparable<RootAttachmentPoint> {

	private final String id;

	/**
	 * Constructs a new {@code RootAttachmentPoint}.
	 * @param id The id of the {@code RootAttachmentPoint}.
	 */
	public RootAttachmentPoint(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

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
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof RootAttachmentPoint)) return false;
		return id.equals(((RootAttachmentPoint) obj).id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
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