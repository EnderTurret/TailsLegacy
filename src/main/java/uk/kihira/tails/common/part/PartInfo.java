/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Stores a bunch of customization data for parts.
 */
public class PartInfo implements Cloneable {

	/**
	 * A map of empty parts so we can reuse them instead of allocating 300,000 empty instances.
	 */
	private static final Map<PartType,PartInfo> EMPTY = new EnumMap<>(PartType.class);

	@Expose
	private final int typeid;
	@Expose
	private final int subid;
	@Expose
	private final int[] tints;
	@Expose
	private final int textureID;
	@Expose
	private final PartType partType;

	private transient ResourceLocation texture;
	public transient boolean needsTextureCompile = true;

	public PartInfo(int type, int subtype, int textureID, int[] tints, PartType partType, ResourceLocation texture) {
		typeid = type;
		subid = subtype;
		this.textureID = textureID;
		this.tints = tints;
		this.partType = partType;
		this.texture = texture;
	}

	public PartInfo(int type, int subtype, int textureID, int tint1, int tint2, int tint3, PartType partType, ResourceLocation texture) {
		this(type, subtype, textureID, new int[] {tint1, tint2, tint3}, partType, texture);
	}

	public static PartInfo none(PartType partType) {
		return EMPTY.computeIfAbsent(partType, Empty::new);
	}

	/**
	 * Whether this {@link PartInfo} is empty.
	 * @return {@code false}. ({@code PartInfo.Empty} overrides this to return {@code true}.)
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Returns the type id, which is a unique identifier for the part.<br>
	 * The type id is the index of the part in the {@link PartRegistry}.
	 * @return The type id.
	 */
	public int getTypeId() {
		return typeid;
	}

	/**
	 * Returns the subtype, which is a unique identifier for the part subtype.<br>
	 * For example, the fluffy tail variants are subtypes of the single fluffy tail.<br>
	 * Subtypes are defined in the {@link PartRegistry}.
	 * @return The subtype.
	 */
	public int getSubType() {
		return subid;
	}

	/**
	 * Returns the tint array, which is an array containing three {@code ints} each of which are packed {@link Color} RGB values.<br>
	 * As {@link PartInfo PartInfos} are supposed to be immutable, please do not modify the array.
	 * @return The tints.
	 */
	public int[] getTints() {
		return tints;
	}

	/**
	 * Returns the texture id, which is a unique identifier for the part texture.<br>
	 * Texture ids are like subtypes, however instead of having additional entries in the part list, they use the texture panel instead.<br>
	 * Texture ids are also defined in the {@link PartRegistry}.
	 * @return The texture ids.
	 */
	public int getTextureId() {
		return textureID;
	}

	/**
	 * @return The part type.
	 */
	public PartType getPartType() {
		return partType;
	}

	/**
	 * Returns the texture location.<br>
	 * If {@link #isEmpty()} is {@code true}, this always returns {@code null}.<br>
	 * Otherwise, this can return {@code null} if the texture needs regenerating.
	 * @return The texture.
	 */
	@Nullable
	public ResourceLocation getTexture() {
		return texture;
	}

	/**
	 * Sets the texture location.
	 * @param texture The new texture.
	 */
	public void setTexture(@Nullable ResourceLocation texture) {
		if (texture == null || this.texture != null && !this.texture.equals(texture)) {
			try {
				Minecraft.getInstance().getTextureManager().deleteTexture(this.texture);
			} catch (Exception ignored) {}

			needsTextureCompile = true;
		} else
			needsTextureCompile = false;
		this.texture = texture;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof PartInfo)) return false;

		final PartInfo partInfo = (PartInfo) o;

		return getTypeId() == partInfo.getTypeId() && getSubType() == partInfo.getSubType() && Arrays.equals(getTints(), partInfo.getTints())
				&& getTextureId() == partInfo.getTextureId() && getPartType() == partInfo.getPartType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(true, getTypeId(), getSubType(), getTints(), getTextureId(), getPartType());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("PartInfo{");
		sb.append(", typeId=").append(guessTypeFromId());
		sb.append(", subid=").append(getSubType());
		sb.append(", tints=").append(Arrays.stream(getTints()).mapToObj(tint -> "0x" + Integer.toHexString(tint)).collect(Collectors.joining(", ", "[", "]")));
		if (getTextureId() != 0)
			sb.append(", textureID=").append(getTextureId());
		sb.append(", partType=").append(getPartType().getId());
		if (getTexture() != null)
			sb.append(", texture=").append(getTexture());
		sb.append("}");

		return sb.toString();
	}

	/**
	 * Attempts to guess the type, and returns a string containing the name.<br>
	 * This is completely hard-coded, and returns only english names.<br>
	 * This is intended for debugging and use in {@link #toString()} so that you don't have to guess what type it is from a vague {@code int}.
	 * @return The guessed type.
	 */
	public String guessTypeFromId() {
		if (getPartType() == PartType.TAIL) {
			if (getTypeId() == 0) return "Fluffy";
			if (getTypeId() == 1) return "Dragon";
			if (getTypeId() == 2) return "Raccoon";
			if (getTypeId() == 3) return "Devil";
			if (getTypeId() == 4) return "Cat";
			if (getTypeId() == 5) return "Bird";
			if (getTypeId() == 6) return "Shark";
			if (getTypeId() == 7) return "Bunny";
		} else if (getPartType() == PartType.EARS) {
			if (getTypeId() == 0) return "Fox";
			if (getTypeId() == 1) return "Cat";
			if (getTypeId() == 2) return "Panda";
			if (getTypeId() == 3) return "Small Cat";
		} else if (getPartType() == PartType.WINGS) {
			if (getTypeId() == 0) return "Wings";
		} else if (getPartType() == PartType.MUZZLE) {
			if (getTypeId() == 0) return "Standard";
			if (getTypeId() == 1) return "Slim";
			if (getTypeId() == 2) return "Thin";
		}

		return "Unknown (" + getTypeId() + ")";
	}

	/**
	 * Returns a copy of this {@link PartInfo}.
	 * @return The copy.
	 */
	public PartInfo deepCopy() {
		final int[] tints = new int[getTints().length];
		for (int i = 0; i < tints.length; i++)
			tints[i] = getTints()[i];

		return new PartInfo(getTypeId(), getSubType(), getTextureId(), tints, getPartType(), getTexture());
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return deepCopy();
	}

	/**
	 * Deserializes this {@link PartInfo} from a {@link JsonObject}.
	 * @param obj The object to deserialize.
	 * @return The deserialized {@link PartInfo}.
	 * @throws JsonParseException
	 */
	public static PartInfo deserialize(JsonObject obj) throws JsonParseException {
		final PartType type = PartType.forId(obj.get("partType").getAsString().toLowerCase(Locale.ROOT));

		if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean())
			return none(type);

		final JsonArray tints = obj.get("tints").getAsJsonArray();

		return new PartInfo(obj.get("typeid").getAsInt(),
				obj.get("subid").getAsInt(),
				obj.get("textureID").getAsInt(),
				tints.get(0).getAsInt(), tints.get(1).getAsInt(), tints.get(2).getAsInt(),
				type, null);
	}

	/**
	 * An empty {@link PartInfo}.
	 * @author EnderTurret
	 */
	private static class Empty extends PartInfo {

		private Empty(PartType partType) {
			super(-1, -1, -1, new int[] {0xFFFF0000, 0xFF00FF00, 0xFF0000FF}, partType, null);
		}

		@Override
		public void setTexture(ResourceLocation texture) {}

		@Override
		public PartInfo deepCopy() {
			return this;
		}

		@Override
		public String toString() {
			return "PartInfo.EMPTY{" + getPartType().getId() + "}";
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	}
}