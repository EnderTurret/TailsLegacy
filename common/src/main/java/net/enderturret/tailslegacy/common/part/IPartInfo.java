/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.part;

import java.awt.Color;
import java.util.Comparator;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.OldJavaUtils;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a side-agnostic view of a part and its associated data.
 * @author EnderTurret
 */
public interface IPartInfo extends Comparable<IPartInfo> {

	/**
	 * @return The empty {@link IPartInfo}.
	 */
	public static IPartInfo empty() {
		return Empty.INSTANCE;
	}

	/**
	 * @return {@code true} if the part is empty.
	 */
	public default boolean isEmpty() {
		return this == Empty.INSTANCE;
	}

	/**
	 * @return A copy of this part.
	 */
	public IPartInfo clone();

	/**
	 * @return The id of the part.
	 */
	public TResourceLocation getPartId();

	/**
	 * Returns the subtype id, which is a unique identifier for the part subtype.
	 * For example, the >1 fluffy tail variants are subtypes of the single fluffy tail.
	 * @return The subtype id.
	 */
	public String getSubTypeId();

	/**
	 * Returns the texture id, which is a unique identifier for the part texture.
	 * @return The texture id.
	 */
	public String getTextureId();

	/**
	 * Returns the tint array, which is an array containing three {@code ints} each of which are packed {@link Color} RGB values.
	 * As {@link IPartInfo IPartInfos} are supposed to be immutable, please do not modify this array.
	 * @return The tints.
	 */
	public int[] getTints();

	/**
	 * Releases the OpenGL texture referenced by this part info.
	 * For obvious reasons, this does nothing on servers.
	 */
	public default void clearGlTexture() {}

	public default String getSubTypeTranslationKey() {
		if (isEmpty()) return "tails.subtype.none";
		return getPartId().t$getNamespace() + ".part." + getPartId().t$getPath() + ".subtype." + getSubTypeId();
	}

	@Nullable
	public default String getFallbackSubTypeTranslationKey() {
		final String id = getSubTypeId();
		if ("default".equals(id) || "standard".equals(id)) return "tails.part.default";
		return null;
	}

	public default String getTextureTranslationKey() {
		if (isEmpty()) return "tails.texture.none";
		return getPartId().t$getNamespace() + ".part." + getPartId().t$getPath() + ".texture." + getTextureId();
	}

	@Nullable
	public default String getFallbackTextureTranslationKey() {
		final String id = getTextureId();
		if ("default".equals(id) || "standard".equals(id)) return "tails.part.default";
		return null;
	}

	public static final Comparator<IPartInfo> COMPARATOR = Comparator.<IPartInfo>nullsFirst(
			Comparator.comparing(IPartInfo::isEmpty)
			.thenComparing(IPartInfo::getPartId, TResourceLocation::t$compareTo)
			.thenComparing(IPartInfo::getSubTypeId)
			.thenComparing(IPartInfo::getTextureId)
			.thenComparing(IPartInfo::getTints, OldJavaUtils::compare3IntArray));

	@Override
	default int compareTo(IPartInfo o) {
		return COMPARATOR.compare(this, o);
	}

	/**
	 * Represents an "empty" {@link IPartInfo}.
	 * @author EnderTurret
	 * @see IPartInfo#empty()
	 */
	@Internal
	public static final class Empty implements IPartInfo {

		/**
		 * The singleton instance.
		 * @see IPartInfo#empty()
		 */
		private static final IPartInfo INSTANCE = new Empty();

		private Empty() {}

		@Override
		public IPartInfo clone() { return this; }

		@Override
		public TResourceLocation getPartId() { return TailsPlatform.get().newResourceLocation("empty"); }

		@Override
		public String getSubTypeId() { return "empty"; }

		@Override
		public String getTextureId() { return "empty"; }

		@Override
		public int[] getTints() {
			return new int[] { 0xFF0000, 0x00FF00, 0x0000FF };
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof IPartInfo)) return false;
			return ((IPartInfo) obj).isEmpty();
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public String toString() {
			return "IPartInfo.Empty.INSTANCE";
		}
	}
}