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
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.ServerPartInfo;

/**
 * Stores a bunch of customization data for parts.
 */
public class ClientPartInfo implements Cloneable, IPartInfo {

	private final IPartInfo delegate;

	private final Part part;
	private final Part.SubType subType;
	private final Part.PartTexture textureId;

	private transient ResourceLocation texture;
	public transient boolean needsTextureCompile = true;

	private ClientPartInfo(@Nullable IPartInfo delegate, @Nullable int[] tints, Part part, Part.SubType subType, Part.PartTexture textureId, @Nullable ResourceLocation texture, boolean empty) {
		if (delegate == null && !empty) {
			if (subType == null)
				subType = part.getSubTypes().get(0);
			if (textureId == null)
				textureId = subType.textures().get(0);

			delegate = new ServerPartInfo(part.getId(), subType.id(), textureId.id(), tints);
		}
		this.delegate = delegate;
		this.part = part;
		this.subType = subType;
		this.textureId = textureId;
		this.texture = texture;
	}

	public ClientPartInfo(IPartInfo delegate, Part part, Part.SubType subType, Part.PartTexture textureId, @Nullable ResourceLocation texture) {
		this(delegate, null, part, subType, textureId, texture, false);
	}

	public ClientPartInfo(IPartInfo delegate, Part part, Part.SubType subType, Part.PartTexture textureId) {
		this(delegate, part, subType, textureId, null);
	}

	public ClientPartInfo(int[] tints, Part part, Part.SubType subType, Part.PartTexture textureId, @Nullable ResourceLocation texture) {
		this(null, tints, part, subType, textureId, texture, false);
	}

	public ClientPartInfo(int[] tints, Part part, Part.SubType subType, Part.PartTexture textureId) {
		this(tints, part, subType, textureId, null);
	}

	public static ClientPartInfo coerce(IPartInfo info) {
		if (info instanceof ClientPartInfo cpi) return cpi;
		if (info.isEmpty()) return empty();

		final Part part = PartRegistry.get(info.getPartId());

		final Part.SubType subType = part == null ? null : part.getSubTypes().stream()
				.filter(st -> st.id().equals(info.getSubTypeId()))
				.findFirst().orElse(null);

		final Part.PartTexture tex = part == null || subType == null ? null : subType.textures().stream()
				.filter(st -> st.id().equals(info.getTextureId()))
				.findFirst().orElse(null);

		return new ClientPartInfo(info, part, subType, tex);
	}

	public static ClientPartInfo empty() {
		return Empty.INSTANCE;
	}

	public IPartInfo unwrap() {
		return delegate;
	}

	public boolean isInvalid() {
		return getPart() == null || getSubType() == null || getPartTexture() == null;
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
		return delegate.getPartId();
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
		return delegate.getSubTypeId();
	}

	/**
	 * Returns the tint array, which is an array containing three {@code ints} each of which are packed {@link Color} RGB values.<br>
	 * As {@link IPartInfo IPartInfos} are supposed to be immutable, please do not modify the array.
	 * @return The tints.
	 */
	@Override
	public int[] getTints() {
		return delegate.getTints();
	}

	/**
	 * Returns the texture id, which is a unique identifier for the part texture.<br>
	 * Texture ids are like subtypes, however instead of having additional entries in the part list, they use the texture panel instead.<br>
	 * Texture ids are also defined in the {@link PartRegistry}.
	 * @return The texture ids.
	 */
	@Override
	public String getTextureId() {
		return delegate.getTextureId();
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

	@Override
	public ClientPartInfo clone() {
		return new ClientPartInfo(delegate.clone(), getPart(), getSubType(), getPartTexture(), getTexture());
	}

	private static final class Empty extends ClientPartInfo {

		private static final ClientPartInfo INSTANCE = new Empty();

		private Empty() {
			super(null, null, null, null, null, null, true);
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
			final IPartInfo info = ServerPartInfo.Serializer.INSTANCE.deserialize(json, typeOfT, context);
			return coerce(info);
		}

		@Override
		public JsonElement serialize(IPartInfo src, Type typeOfSrc, JsonSerializationContext context) {
			return ServerPartInfo.Serializer.INSTANCE.serialize(src, typeOfSrc, context);
		}
	}
}