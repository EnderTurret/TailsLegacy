/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.part;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.client.part.PartRegistry.PartReference;
import uk.kihira.tails.common2.part.IPartInfo;
import uk.kihira.tails.common2.part.ServerPartInfo;

/**
 * Represents the client-side version of {@link IPartInfo}.
 * Mostly, it provides more context to the otherwise-useless part/subtype/texture ids.
 * @see IPartInfo
 */
public class ClientPartInfo implements Cloneable, IPartInfo {

	private final IPartInfo delegate;

	private final PartReference part;
	private final String subType;
	private final String textureId;

	private transient ResourceLocation texture;

	private ClientPartInfo(@Nullable IPartInfo delegate, @Nullable int[] tints, PartReference part, String subType, String textureId, @Nullable ResourceLocation texture, boolean empty) {
		if (delegate == null && !empty) {
			if (subType == null)
				subType = part.get().getSubTypes().get(0).id();
			if (textureId == null)
				textureId = part.get().getSubType(subType).textures().get(0).id();

			delegate = new ServerPartInfo(part.id(), subType, textureId, tints);
		}
		this.delegate = delegate;
		this.part = part;
		this.subType = subType;
		this.textureId = textureId;
		this.texture = texture;
	}

	public ClientPartInfo(IPartInfo delegate, PartReference part, String subType, String textureId, @Nullable ResourceLocation texture) {
		this(delegate, null, part, subType, textureId, texture, false);
	}

	public ClientPartInfo(IPartInfo delegate, PartReference part, String subType, String textureId) {
		this(delegate, part, subType, textureId, null);
	}

	public ClientPartInfo(int[] tints, PartReference part, String subType, String textureId, @Nullable ResourceLocation texture) {
		this(null, tints, part, subType, textureId, texture, false);
	}

	public ClientPartInfo(int[] tints, PartReference part, String subType, String textureId) {
		this(tints, part, subType, textureId, null);
	}

	public ClientPartInfo(int[] tints, PartReference part, String subType) {
		this(tints, part, subType, part.get().getSubType(subType).textures().get(0).id());
	}

	public ClientPartInfo(int[] tints, PartReference part) {
		this(tints, part, part.get().getSubTypes().get(0).id());
	}

	/**
	 * Coerces the given {@link IPartInfo} into a {@link ClientPartInfo}.
	 * @param info The {@link IPartInfo} to coerce.
	 * @return The coerced {@link ClientPartInfo}.
	 */
	public static ClientPartInfo coerce(IPartInfo info) {
		if (info instanceof ClientPartInfo cpi) return cpi;
		if (info.isEmpty()) return empty();

		final PartReference part = PartRegistry.reference(info.getPartId());

		return new ClientPartInfo(info, part, info.getSubTypeId(), info.getTextureId());
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
	 * Convenience method for {@link PartRenderRegistry#getRenderer(Part) PartRenderRegistry.getRenderer(getPart())}.
	 * @return The {@link PartRenderer} registered for this part.
	 */
	public PartRenderer getRenderer() {
		return PartRenderRegistry.getRenderer(getPart());
	}

	/**
	 * Resolved version of {@link #getPartId()}.
	 * @return The resolved part.
	 */
	public Part getPart() {
		return part != null ? part.get() : null;
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
		return part == null ? null : part.get().getSubType(subType);
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
		final Part.SubType subType = getSubType();
		if (subType == null) return null;
		return subType.getTexture(textureId);
	}

	@Override
	public String getTextureId() {
		return delegate.getTextureId();
	}

	/**
	 * Returns the texture location.
	 * If {@link #isEmpty()} is {@code true}, this always returns {@code null}.
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

		return isEmpty() == partInfo.isEmpty()
				&& Objects.equals(getPartId(), partInfo.getPartId())
				&& Objects.equals(getSubTypeId(), partInfo.getSubTypeId())
				&& Objects.equals(getTextureId(), partInfo.getTextureId())
				&& Arrays.equals(getTints(), partInfo.getTints());
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
		return new ClientPartInfo(delegate.clone(), part, subType, textureId, null);
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
		public ResourceLocation getPartId() { return ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, "empty"); }

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
}