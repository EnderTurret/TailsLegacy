/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

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

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.Parts;

/**
 * Stores a bunch of customization data for parts.
 */
public class ClientPartInfo implements Cloneable, IPartInfo {

	private final Part part;
	private final Part.SubType subType;
	private final int[] tints;
	private final Part.PartTexture textureId;

	private transient ResourceLocation texture;
	public transient boolean needsTextureCompile = true;

	public ClientPartInfo(Part part, Part.SubType subType, Part.PartTexture textureId, int[] tints, @Nullable ResourceLocation texture) {
		this.part = part;
		this.subType = subType;
		this.textureId = textureId;
		this.tints = tints;
		this.texture = texture;
	}

	public ClientPartInfo(Part part, Part.SubType subType, Part.PartTexture textureId, int tint1, int tint2, int tint3, @Nullable ResourceLocation texture) {
		this(part, subType, textureId, new int[] {tint1, tint2, tint3}, texture);
	}

	public static ClientPartInfo coerce(IPartInfo info) {
		if (info instanceof ClientPartInfo cpi) return cpi;
		if (info.isEmpty()) return empty();

		final Part part = PartRegistry.get(info.getPartId());
		final Part.SubType subType = part.getSubTypes().stream()
				.filter(st -> st.id().equals(info.getSubTypeId()))
				.findFirst().orElse(part.getSubTypes().get(0));
		final Part.PartTexture tex = subType.textures().stream()
				.filter(st -> st.id().equals(info.getTextureId()))
				.findFirst().orElse(subType.textures().get(0));

		return new ClientPartInfo(part, subType, tex, info.getTints().clone(), null);
	}

	public static ClientPartInfo empty() {
		return Empty.INSTANCE;
	}

	@Override
	public PartType getType() {
		return part.getType();
	}

	public Part getPart() {
		return part;
	}

	/**
	 * Returns the part id, which is a unique identifier for the part.
	 * @return The part id.
	 */
	@Override
	public ResourceLocation getPartId() {
		return part.getId();
	}

	/**
	 * Returns the sub type, which is a unique identifier for the part sub type.<br>
	 * For example, the fluffy tail variants are sub types of the single fluffy tail.<br>
	 * Sub types are defined in the {@link PartRegistry}.
	 * @return The sub type.
	 */
	public Part.SubType getSubType() {
		return subType;
	}

	@Override
	public String getSubTypeId() {
		return subType.id();
	}

	/**
	 * Returns the tint array, which is an array containing three {@code ints} each of which are packed {@link Color} RGB values.<br>
	 * As {@link IPartInfo IPartInfos} are supposed to be immutable, please do not modify the array.
	 * @return The tints.
	 */
	@Override
	public int[] getTints() {
		return tints;
	}

	/**
	 * Returns the texture id, which is a unique identifier for the part texture.<br>
	 * Texture ids are like subtypes, however instead of having additional entries in the part list, they use the texture panel instead.<br>
	 * Texture ids are also defined in the {@link PartRegistry}.
	 * @return The texture ids.
	 */
	@Override
	public String getTextureId() {
		return textureId.id();
	}

	public Part.PartTexture getPartTexture() {
		return textureId;
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
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientPartInfo partInfo)) return false;

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
		sb.append(", subType=").append(getSubType().id());
		sb.append(", tints=").append(Arrays.stream(getTints()).mapToObj(tint -> "0x" + Integer.toHexString(tint)).collect(Collectors.joining(", ", "[", "]")));
		if (getTextureId() != null)
			sb.append(", textureId=").append(getTextureId());
		if (getTexture() != null)
			sb.append(", texture=").append(getTexture());
		sb.append("}");

		return sb.toString();
	}

	/**
	 * Returns a copy of this {@link ClientPartInfo}.
	 * @return The copy.
	 */
	public ClientPartInfo deepCopy() {
		final int[] tints = new int[getTints().length];
		for (int i = 0; i < tints.length; i++)
			tints[i] = getTints()[i];

		return new ClientPartInfo(getPart(), getSubType(), getPartTexture(), tints, getTexture());
	}

	@Override
	public ClientPartInfo clone() {
		return deepCopy();
	}

	private static final class Empty extends ClientPartInfo {

		private static final ClientPartInfo INSTANCE = new Empty();

		private Empty() {
			super(null, null, null, null, null);
		}

		@Override
		public ClientPartInfo clone() { return this; }

		@Override
		public boolean isEmpty() { return true; }

		@Override
		public PartType getType() { return null; }

		@Override
		public ResourceLocation getPartId() { return null; }

		@Override
		public String getSubTypeId() { return null; }

		@Override
		public String getTextureId() { return null; }

		@Override
		public int[] getTints() { return new int[] { 0xFFFFFF, 0xFFFFFF, 0xFFFFFF }; }

		@Override
		public void clearGlTexture() {}

		@Override
		public void setTexture(ResourceLocation texture) {}
	}

	public static class Serializer implements JsonSerializer<IPartInfo>, JsonDeserializer<IPartInfo> {

		@Override
		public IPartInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject obj = json.getAsJsonObject();

			if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean())
				return empty();

			final Part part;

			if (obj.has("partType")) {
				final PartType type = PartType.forId(obj.get("partType").getAsString().toLowerCase(Locale.ROOT));
				final int id = obj.get("typeid").getAsInt();
				part = PartRegistry.byLegacyId(type, id);
			} else if (!obj.has("id"))
				throw new JsonParseException("Missing part id!");
			else {
				final String id = obj.get("id").getAsString();
				if ("tails:empty".equals(id)) return empty();

				final ResourceLocation realId = new ResourceLocation(id);
				final ResourceLocation newId = Parts.remapId(realId);

				if (realId != newId)
					Tails.LOGGER.info("Remapped part id: {} → {}.", realId, newId);

				part = PartRegistry.get(newId);

				if (part == null)
					throw new JsonParseException("Unknown part id: \"" + id + "\"");
			}

			if (part.getSubTypes().isEmpty())
				throw new IllegalStateException("Part is missing sub types!");

			final Part.SubType subType;
			final Part.PartTexture texture;

			if (obj.has("subid")) {
				int idx = obj.get("subid").getAsInt();
				if (idx >= part.getSubTypes().size()) idx = 0;
				subType = part.getSubTypes().get(idx);
				Tails.LOGGER.info("Remapped sub type {} → {}", idx, subType.id());
			} else {
				final String id = obj.get("subType").getAsString();
				subType = part.getSubTypes().stream()
						.filter(st -> st.id().equals(id))
						.findFirst().orElseThrow();
			}

			if (subType.textures().isEmpty())
				throw new IllegalStateException("Sub type is missing textures!");

			if (obj.has("textureID")) {
				int idx = obj.get("textureID").getAsInt();
				if (idx >= subType.textures().size()) idx = 0;
				texture = subType.textures().get(idx);
				Tails.LOGGER.info("Remapped texture {} → {}", idx, texture.id());
			} else {
				final String id = obj.get("textureId").getAsString();
				texture = subType.textures().stream()
						.filter(st -> st.id().equals(id))
						.findFirst().orElseThrow();
			}

			final JsonArray tints = obj.get("tints").getAsJsonArray();

			return new ClientPartInfo(part, subType, texture,
					tints.get(0).getAsInt(), tints.get(1).getAsInt(), tints.get(2).getAsInt(),
					null);
		}

		@Override
		public JsonElement serialize(IPartInfo src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject obj = new JsonObject();

			obj.addProperty("id", src.getPartId().toString());

			if (!src.isEmpty()) {
				obj.addProperty("subType", src.getSubTypeId());
				obj.addProperty("textureId", src.getTextureId());

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