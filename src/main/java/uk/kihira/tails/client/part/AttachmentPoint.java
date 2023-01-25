package uk.kihira.tails.client.part;

import net.minecraft.network.chat.Component;

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
 * @param root The {@code RootAttachmentPoint}. Represents the "body part" of the {@code AttachmentPoint}.
 * @param subId The id of the {@code AttachmentPoint} itself. Corresponds to {@code everywhere} in {@code body/everywhere}.
 * @param id The full id of the {@code AttachmentPoint}. This is the id you would see in part files.
 * @see AttachmentPoints
 * @author EnderTurret
 */
public record AttachmentPoint(RootAttachmentPoint root, String subId, String id) implements Comparable<AttachmentPoint> {

	/**
	 * @return A translatable component for this {@code AttachmentPoint}.
	 */
	public Component toComponent() {
		return Component.translatable("tails.attachment." + id);
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