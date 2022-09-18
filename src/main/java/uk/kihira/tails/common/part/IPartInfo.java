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
			return new int[] { 0xFFFFFF, 0xFFFFFF, 0xFFFFFF };
		}
	}
}