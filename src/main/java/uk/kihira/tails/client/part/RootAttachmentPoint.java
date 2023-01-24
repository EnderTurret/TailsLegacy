package uk.kihira.tails.client.part;

import java.util.NavigableSet;

import net.minecraft.network.chat.Component;

public record RootAttachmentPoint(String id) implements Comparable<RootAttachmentPoint> {

	public Component toComponent() {
		return Component.translatable("tails.attachment." + id);
	}

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