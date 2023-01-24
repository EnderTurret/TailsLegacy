package uk.kihira.tails.client.part;

public record AttachmentPoint(RootAttachmentPoint root, String subId, String id) implements Comparable<AttachmentPoint> {

	@Override
	public int compareTo(AttachmentPoint o) {
		return o == null ? 1 : id.compareTo(o.id);
	}

	@Override
	public String toString() {
		return id;
	}
}