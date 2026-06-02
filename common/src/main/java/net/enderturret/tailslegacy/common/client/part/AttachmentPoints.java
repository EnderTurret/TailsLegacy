/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.Nullable;

/**
 * Contains all of the {@link AttachmentPoint AttachmentPoints} referenced by parts.
 * @see AttachmentPoint
 * @author EnderTurret
 */
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

	/**
	 * @return An unmodifiable view of all created attachment points.
	 */
	public static NavigableSet<AttachmentPoint> getAll() {
		return ALL_ATTACHMENT_POINTS_VIEW;
	}

	/**
	 * @param id The id of the {@code RootAttachmentPoint} to retrieve.
	 * @return The {@code RootAttachmentPoint} with the given id, or {@code null} if one doesn't exist.
	 */
	@Nullable
	public static RootAttachmentPoint getRoot(String id) {
		return ROOTS.get(id);
	}

	/**
	 * @return An unmodifiable view of all created root attachment points.
	 */
	public static NavigableSet<RootAttachmentPoint> getRoots() {
		return ROOT_ATTACHMENT_POINTS_VIEW;
	}

	/**
	 * @param root The {@code RootAttachmentPoint} to retrieve the children of.
	 * @return An unmodifiable view of the {@code AttachmentPoints} listed under the given {@code RootAttachmentPoint}.
	 */
	public static NavigableSet<AttachmentPoint> getAll(RootAttachmentPoint root) {
		return Collections.unmodifiableNavigableSet(ATTACHMENT_POINTS.getOrDefault(root, Collections.emptyNavigableSet()));
	}

	/**
	 * Retrieves the {@code AttachmentPoint} specified by the given "full id."
	 * @param fullId The id of the {@code AttachmentPoint}, beginning with the id of the {@code RootAttachmentPoint}.
	 * @return The corresponding {@code AttachmentPoint}, or {@code null} if one wasn't found.
	 * @throws IllegalArgumentException If the path is malformed in some way.
	 */
	@Nullable
	public static AttachmentPoint get(String fullId) throws IllegalArgumentException {
		final int idx = fullId.indexOf('/');
		if (idx == -1) throw new IllegalArgumentException("Attachment point must have two components: " + fullId);

		for (AttachmentPoint ap : getAll())
			if (ap.id().equals(fullId))
				return ap;

		return null;
	}

	/**
	 * A version of {@link #get(String)} that creates a new {@code AttachmentPoint} if one doesn't already exist.
	 * @param fullId The id of the {@code AttachmentPoint}, beginning with the id of the {@code RootAttachmentPoint}.
	 * @return The corresponding {@code AttachmentPoint}.
	 * @throws IllegalArgumentException If the path is malformed in some way.
	 */
	public static AttachmentPoint getOrCreate(String fullId) throws IllegalArgumentException {
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