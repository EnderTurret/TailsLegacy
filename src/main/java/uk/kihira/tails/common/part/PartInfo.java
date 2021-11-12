/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.common.Tails;

/**
 * Stores a bunch of customization data for parts.
 */
public class PartInfo implements Cloneable {

	private static final PartInfo EMPTY = new Empty();

	private final ResourceLocation partId;
	private final int subid;
	private final int[] tints;
	private final int textureID;

	private transient ResourceLocation texture;
	public transient boolean needsTextureCompile = true;

	public PartInfo(ResourceLocation partId, int subtype, int textureID, int[] tints, @Nullable ResourceLocation texture) {
		this.partId = partId;
		subid = subtype;
		this.textureID = textureID;
		this.tints = tints;
		this.texture = texture;
	}

	public PartInfo(ResourceLocation partId, int subtype, int textureID, int tint1, int tint2, int tint3, @Nullable ResourceLocation texture) {
		this(partId, subtype, textureID, new int[] {tint1, tint2, tint3}, texture);
	}

	public static PartInfo none() {
		return EMPTY;
	}

	/**
	 * Whether this {@link PartInfo} is empty.
	 * @return {@code false}. ({@code PartInfo.Empty} overrides this to return {@code true}.)
	 */
	public boolean isEmpty() {
		return false;
	}

	public Part getPart() {
		return PartRegistry.get(partId);
	}

	/**
	 * Returns the part id, which is a unique identifier for the part.
	 * @return The part id.
	 */
	public ResourceLocation getPartId() {
		return partId;
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
				Tails.PROXY.deleteTexture(this.texture);
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

		return getPartId().equals(partInfo.getPartId()) && getSubType() == partInfo.getSubType() && Arrays.equals(getTints(), partInfo.getTints())
				&& getTextureId() == partInfo.getTextureId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPartId(), getSubType(), getTints(), getTextureId());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("PartInfo{");
		sb.append("partId=").append(getPartId());
		sb.append(", subid=").append(getSubType());
		sb.append(", tints=").append(Arrays.stream(getTints()).mapToObj(tint -> "0x" + Integer.toHexString(tint)).collect(Collectors.joining(", ", "[", "]")));
		if (getTextureId() != 0)
			sb.append(", textureID=").append(getTextureId());
		if (getTexture() != null)
			sb.append(", texture=").append(getTexture());
		sb.append("}");

		return sb.toString();
	}

	/**
	 * Returns a copy of this {@link PartInfo}.
	 * @return The copy.
	 */
	public PartInfo deepCopy() {
		final int[] tints = new int[getTints().length];
		for (int i = 0; i < tints.length; i++)
			tints[i] = getTints()[i];

		return new PartInfo(getPartId(), getSubType(), getTextureId(), tints, getTexture());
	}

	@Override
	public PartInfo clone() {
		return deepCopy();
	}

	/**
	 * An empty {@link PartInfo}.
	 * @author EnderTurret
	 */
	private static class Empty extends PartInfo {

		private Empty() {
			super(new ResourceLocation(Tails.MOD_ID, "empty"), 0, 0, new int[] {0xFFFF0000, 0xFF00FF00, 0xFF0000FF}, null);
		}

		@Override
		public Part getPart() {
			return null;
		}

		@Override
		public void setTexture(ResourceLocation texture) {}

		@Override
		public PartInfo deepCopy() {
			return this;
		}

		@Override
		public String toString() {
			return "PartInfo.EMPTY";
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	public static class Serializer implements JsonSerializer<PartInfo>, JsonDeserializer<PartInfo> {

		@Override
		public PartInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject obj = json.getAsJsonObject();

			if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean())
				return none();

			final Part part;

			if (obj.has("partType")) {
				final PartType type = PartType.forId(obj.get("partType").getAsString().toLowerCase(Locale.ROOT));
				final int id = obj.get("typeid").getAsInt();
				part = PartRegistry.byNumericId(type, id);
			} else {
				final String id = obj.get("id").getAsString();
				if ("tails:empty".equals(id)) return none();
				part = PartRegistry.get(new ResourceLocation(id));
			}

			final JsonArray tints = obj.get("tints").getAsJsonArray();

			return new PartInfo(part.getId(),
					obj.get("subid").getAsInt(),
					obj.get("textureID").getAsInt(),
					tints.get(0).getAsInt(), tints.get(1).getAsInt(), tints.get(2).getAsInt(),
					null);
		}

		@Override
		public JsonElement serialize(PartInfo src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject obj = new JsonObject();

			obj.addProperty("id", src.getPartId().toString());

			if (!src.isEmpty()) {
				obj.addProperty("subid", src.getSubType());
				obj.addProperty("textureID", src.getTextureId());

				final JsonArray tints = new JsonArray();
				tints.add(src.getTints()[0]);
				tints.add(src.getTints()[1]);
				tints.add(src.getTints()[2]);
				obj.add("tints", tints);
			}

			return obj;
		}
	}
}