package uk.kihira.tails.client.part;

import java.util.NavigableSet;

public record RootAttachmentPoint(String id) implements Comparable<RootAttachmentPoint> {

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