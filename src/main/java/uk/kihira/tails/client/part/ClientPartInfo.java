/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.ServerPartInfo;

/**
 * Represents the client-side version of {@link IPartInfo}.
 * Mostly, it provides more context to the otherwise-useless part/subtype/texture ids.
 * @see IPartInfo
 */
public class ClientPartInfo implements Cloneable, IPartInfo {

	private final IPartInfo delegate;

	private final Part part;
	private final Part.SubType subType;
	private final Part.PartTexture textureId;

	private transient ResourceLocation texture;

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

	/**
	 * Coerces the given {@link IPartInfo} into a {@link ClientPartInfo}.
	 * @param info The {@link IPartInfo} to coerce.
	 * @return The coerced {@link ClientPartInfo}.
	 */
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

	/**
	 * @return The empty {@link ClientPartInfo}.
	 */
	public static ClientPartInfo empty() {
		return Empty.INSTANCE;
	}

	/**
	 * @return The wrapped {@link IPartInfo}.
	 */
	public IPartInfo unwrap() {
		return delegate;
	}

	/**
	 * Determines whether this part is "invalid," meaning that the part, subtype, or texture is {@code null}.
	 * This can happen if someone is using a custom part that you don't possess.
	 * @return {@code true} if the part is invalid.
	 */
	public boolean isInvalid() {
		return getPart() == null || getSubType() == null || getPartTexture() == null;
	}

	/**
	 * Resolved version of {@link #getPartId()}.
	 * @return The resolved part.
	 */
	public Part getPart() {
		return part;
	}

	@Override
	public ResourceLocation getPartId() {
		return delegate.getPartId();
	}

	/**
	 * Resolved version of {@link #getSubTypeId()}.
	 * @return The resolved subtype.
	 */
	public Part.SubType getSubType() {
		return subType;
	}

	@Override
	public String getSubTypeId() {
		return delegate.getSubTypeId();
	}

	/**
	 * Resolved version of {@link #getPartTexture()}.
	 * @return The resolved texture.
	 */
	public Part.PartTexture getPartTexture() {
		return textureId;
	}

	@Override
	public String getTextureId() {
		return delegate.getTextureId();
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

	@Override
	public int[] getTints() {
		return delegate.getTints();
	}

	/**
	 * Sets the texture location, possibly releasing the old texture reference.
	 * @param texture The new texture.
	 */
	public void setTexture(@Nullable ResourceLocation texture) {
		final ResourceLocation old = this.texture;

		if (old != null && !old.equals(texture))
			clearGlTexture();

		this.texture = texture;
	}

	/**
	 * Checks to make sure a texture is present, generating one if necessary.
	 * @param uuid The {@link UUID} of the entity being rendered.
	 * @param force Normally, the texture is only generated if it is missing. If {@code true}, this forces the texture to be regenerated regardless.
	 */
	public void checkTexture(UUID uuid, boolean force) {
		if (!isEmpty() && !isInvalid() && (force || getTexture() == null))
			setTexture(TextureHelper.generateTexture(uuid, this));
	}

	@Override
	public void clearGlTexture() {
		if (texture != null) {
			TextureHelper.release(texture);
			texture = null;
		}
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IPartInfo partInfo)) return false;

		return isEmpty() == partInfo.isEmpty() && Objects.equals(getPartId(), partInfo.getPartId()) && Objects.equals(getSubTypeId(), partInfo.getSubTypeId())
				&& Arrays.equals(getTints(), partInfo.getTints()) && Objects.equals(getTextureId(), partInfo.getTextureId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPartId(), getSubTypeId(), getTints(), getTextureId());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("ClientPartInfo{");
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
		return new ClientPartInfo(delegate.clone(), getPart(), getSubType(), getPartTexture(), null);
	}

	/**
	 * Represents an "empty" {@link ClientPartInfo}.
	 * @author EnderTurret
	 * @see ClientPartInfo#empty()
	 */
	private static final class Empty extends ClientPartInfo {

		/**
		 * The singleton instance.
		 * @see ClientPartInfo#empty()
		 */
		private static final ClientPartInfo INSTANCE = new Empty();

		private Empty() {
			super(null, null, null, null, null, null, true);
		}

		@Override
		public ClientPartInfo clone() { return this; }

		@Override
		public boolean isEmpty() { return true; }

		@Override
		public ResourceLocation getPartId() { return new ResourceLocation(Tails.MOD_ID, "empty"); }

		@Override
		public String getSubTypeId() { return "empty"; }

		@Override
		public String getTextureId() { return "empty"; }

		@Override
		public int[] getTints() { return Part.DEFAULT_TINTS.clone(); }

		@Override
		public void clearGlTexture() {}

		@Override
		public void setTexture(ResourceLocation texture) {}

		@Override
		public String toString() {
			return "ClientPartInfo.Empty.INSTANCE";
		}
	}

	/**
	 * A serializer for {@link ClientPartInfo}.
	 * @author EnderTurret
	 * @see LocalPartManager#GSON
	 */
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