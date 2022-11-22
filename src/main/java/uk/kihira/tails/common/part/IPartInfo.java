/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.awt.Color;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;

/**
 * Represents a side-agnostic view of a part and its associated data.
 * @author EnderTurret
 */
public interface IPartInfo {

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
	 * @return The part type.
	 */
	public PartType getType();

	/**
	 * @return The id of the part.
	 */
	public ResourceLocation getPartId();

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
		public PartType getType() { return null; }

		@Override
		public ResourceLocation getPartId() { return new ResourceLocation(Tails.MOD_ID, "empty"); }

		@Override
		public String getSubTypeId() { return "empty"; }

		@Override
		public String getTextureId() { return "empty"; }

		@Override
		public int[] getTints() {
			return new int[] { 0xFF0000, 0x00FF00, 0x0000FF };
		}
	}
}