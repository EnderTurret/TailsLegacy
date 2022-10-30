/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;

public interface IPartInfo {

	public static IPartInfo empty() {
		return Empty.INSTANCE;
	}

	public default boolean isEmpty() {
		return this == Empty.INSTANCE;
	}

	public IPartInfo clone();

	public PartType getType();
	public ResourceLocation getPartId();
	public String getSubTypeId();
	public String getTextureId();
	public int[] getTints();

	public default void clearGlTexture() {}

	public static final class Empty implements IPartInfo {

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