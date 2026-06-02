/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Objects;

/**
 * <p>Represents an "attachment point." This is rather similar to the old "part type" system.</p>
 *
 * <p>Each {@code AttachmentPoint} has a {@link RootAttachmentPoint}, which represents a body part of some kind (like the head or body).
 * {@code AttachmentPoints} themselves can be thought of as locations on that body part.</p>
 *
 * <p>{@code AttachmentPoints} are calculated based on what the parts ask for -- if
 * the part {@code my_part} says its attachment point is {@code body/everywhere},
 * then the corresponding {@code AttachmentPoint} will be created.
 * These {@code AttachmentPoints} can be found in {@link AttachmentPoints}.</p>
 * @see AttachmentPoints
 * @author EnderTurret
 */
public final class AttachmentPoint implements Comparable<AttachmentPoint> {

	private final RootAttachmentPoint root;
	private final String subId;
	private final String id;

	/**
	 * Constructs a new {@code AttachmentPoint}.
	 * @param root The {@code RootAttachmentPoint}. Represents the "body part" of the {@code AttachmentPoint}.
	 * @param subId The id of the {@code AttachmentPoint} itself. Corresponds to {@code everywhere} in {@code body/everywhere}.
	 * @param id The full id of the {@code AttachmentPoint}. This is the id you would see in part files.
	 */
	public AttachmentPoint(RootAttachmentPoint root, String subId, String id) {
		this.root = root;
		this.subId = subId;
		this.id = id;
	}

	/**
	 * Returns the {@code RootAttachmentPoint}. Represents the "body part" of the {@code AttachmentPoint}.
	 * @return The {@code RootAttachmentPoint}.
	 */
	public RootAttachmentPoint root() {
		return root;
	}

	/**
	 * Returns the id of the {@code AttachmentPoint} itself. Corresponds to {@code everywhere} in {@code body/everywhere}.
	 * @return The id.
	 */
	public String subId() {
		return subId;
	}

	/**
	 * Returns the full id of the {@code AttachmentPoint}. This is the id you would see in part files.
	 * @return The full id.
	 */
	public String id() {
		return id;
	}

	public String translationKey() {
		return "tails.attachment." + id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof AttachmentPoint)) return false;
		final AttachmentPoint p = (AttachmentPoint) obj;
		return root.equals(p.root) && subId.equals(p.subId) && id.equals(p.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(root, subId, id);
	}

	@Override
	public int compareTo(AttachmentPoint o) {
		return o == null ? 1 : id.compareTo(o.id);
	}

	@Override
	public String toString() {
		return id;
	}
}