package uk.kihira.tails.client.part;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.Nullable;

public class AttachmentPoints {

	private static final Map<String, RootAttachmentPoint> ROOTS = new TreeMap<>();
	private static final Map<RootAttachmentPoint, NavigableSet<AttachmentPoint>> ATTACHMENT_POINTS = new TreeMap<>();
	private static final TreeSet<RootAttachmentPoint> ROOT_ATTACHMENT_POINTS = new TreeSet<>();
	private static final NavigableSet<RootAttachmentPoint> ROOT_ATTACHMENT_POINTS_VIEW = Collections.unmodifiableNavigableSet(ROOT_ATTACHMENT_POINTS);
	private static final TreeSet<AttachmentPoint> ALL_ATTACHMENT_POINTS = new TreeSet<>();
	private static final NavigableSet<AttachmentPoint> ALL_ATTACHMENT_POINTS_VIEW = Collections.unmodifiableNavigableSet(ALL_ATTACHMENT_POINTS);

	static void clear() {
		ROOTS.clear();
		ATTACHMENT_POINTS.clear();
		ALL_ATTACHMENT_POINTS.clear();
	}

	public static NavigableSet<AttachmentPoint> getAll() {
		return ALL_ATTACHMENT_POINTS_VIEW;
	}

	@Nullable
	public static RootAttachmentPoint getRoot(String id) {
		return ROOTS.get(id);
	}

	public static NavigableSet<RootAttachmentPoint> getRoots() {
		return ROOT_ATTACHMENT_POINTS_VIEW;
	}

	public static NavigableSet<AttachmentPoint> getAll(RootAttachmentPoint root) {
		return Collections.unmodifiableNavigableSet(ATTACHMENT_POINTS.getOrDefault(root, Collections.emptyNavigableSet()));
	}

	@Nullable
	public static AttachmentPoint get(String fullId) {
		final int idx = fullId.indexOf('/');
		if (idx == -1) throw new IllegalArgumentException("Attachment point must have two components: " + fullId);

		for (AttachmentPoint ap : getAll())
			if (ap.id().equals(fullId))
				return ap;

		return null;
	}

	public static AttachmentPoint getOrCreate(String fullId) {
		final int idx = fullId.indexOf('/');
		if (idx == -1) throw new IllegalArgumentException("Attachment point must have two components: " + fullId);

		final String rootId = fullId.substring(0, idx);
		final String subId = fullId.substring(idx + 1);

		final RootAttachmentPoint root = ROOTS.computeIfAbsent(rootId, k -> {
			final RootAttachmentPoint ret = new RootAttachmentPoint(k);
			ROOT_ATTACHMENT_POINTS.add(ret);
			return ret;
		});

		final Set<AttachmentPoint> points = ATTACHMENT_POINTS.computeIfAbsent(root, k -> new TreeSet<>());

		for (AttachmentPoint ap : points)
			if (ap.subId().equals(subId))
				return ap;

		final AttachmentPoint ret = new AttachmentPoint(root, subId, fullId);
		points.add(ret);
		ALL_ATTACHMENT_POINTS.add(ret);

		return ret;
	}
}