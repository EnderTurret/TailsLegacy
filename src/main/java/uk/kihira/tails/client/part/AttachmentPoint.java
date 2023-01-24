package uk.kihira.tails.client.part;

import net.minecraft.network.chat.Component;

public record AttachmentPoint(RootAttachmentPoint root, String subId, String id) implements Comparable<AttachmentPoint> {

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