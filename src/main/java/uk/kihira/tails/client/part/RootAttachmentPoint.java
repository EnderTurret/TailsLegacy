package uk.kihira.tails.client.part;

import java.util.NavigableSet;

import net.minecraft.network.chat.Component;

/**
 * Represents a "root attachment point," which can be thought of as the general body part of {@link AttachmentPoint AttachmentPoints}.
 * @param id The id of the {@code RootAttachmentPoint}.
 * @see AttachmentPoint
 * @see AttachmentPoints
 * @author EnderTurret
 */
public record RootAttachmentPoint(String id) implements Comparable<RootAttachmentPoint> {

	/**
	 * @return A translatable component for this {@code RootAttachmentPoint}.
	 */
	public Component toComponent() {
		return Component.translatable("tails.attachment." + id);
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