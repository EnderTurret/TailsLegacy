/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.part;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Holds a {@link IPartInfo} for every part type.
 */
public class PartsData {

	/**
	 * The singleton empty {@link PartsData}.
	 */
	public static final PartsData EMPTY = new PartsData() {
		@Override public void addPartInfo(IPartInfo partInfo) {}
		@Override public Set<IPartInfo> getPartInfos() { return Collections.emptySet(); }
		@Override public void clearTextures() {}
		@Override public PartsData deepCopy() { return this; }
		@Override public String toString() { return "PartsData#EMPTY"; }
		@Override public boolean isEmpty() { return true; }
	};

	protected final Set<IPartInfo> parts = new TreeSet<>();

	/**
	 * The version of the part format.
	 * 2 is the current version.
	 */
	public final int version = 2;

	public PartsData() {}

	public PartsData(Set<IPartInfo> parts) {
		this();
		this.parts.addAll(parts);
	}

	protected IPartInfo empty() {
		return IPartInfo.empty();
	}

	public boolean isEmpty() {
		return false;
	}

	/**
	 * Adds the given part info.
	 * @param partInfo The part info.
	 */
	public void addPartInfo(IPartInfo partInfo) {
		if (partInfo.isEmpty()) return;
		parts.add(partInfo);
	}

	public Set<IPartInfo> getPartInfos() {
		return Collections.unmodifiableSet(parts);
	}

	/**
	 * Clears all textures from each {@link IPartInfo}.
	 */
	public void clearTextures() {
		for (IPartInfo partInfo : parts)
			if (partInfo != null) partInfo.clearGlTexture();
	}

	/**
	 * Returns a copy of this {@link PartsData}.
	 * @return The copy.
	 */
	public PartsData deepCopy() {
		final Set<IPartInfo> data = new LinkedHashSet<>();
		for (IPartInfo info : parts)
			data.add(info.clone());

		return new PartsData(data);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PartsData)) return false;
		return parts.equals(((PartsData) o).parts);
	}

	@Override
	public int hashCode() {
		return parts.hashCode();
	}

	@Override
	public String toString() {
		return "PartsData{" + parts.stream()
				.filter(e -> !e.isEmpty())
				.map(IPartInfo::toString)
				.collect(Collectors.joining(", ")) + '}';
	}
}