/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TextureHelper;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.model.animation.AnimatorStorage;
import net.enderturret.tailslegacy.common.client.part.PartRegistry.PartReference;
import net.enderturret.tailslegacy.common.client.render.PartRenderRegistry;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;
import net.enderturret.tailslegacy.common.part.IPartInfo;
import net.enderturret.tailslegacy.common.part.ServerPartInfo;

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

	private transient @Nullable TResourceLocation texture;
	private transient @Nullable AnimatorStorage animatorStorage;

	private ClientPartInfo(@Nullable IPartInfo delegate, @Nullable int[] tints, PartReference part, String subType, String textureId, @Nullable TResourceLocation texture, boolean empty) {
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

	public ClientPartInfo(IPartInfo delegate, PartReference part, String subType, String textureId, @Nullable TResourceLocation texture) {
		this(delegate, null, part, subType, textureId, texture, false);
	}

	public ClientPartInfo(IPartInfo delegate, PartReference part, String subType, String textureId) {
		this(delegate, part, subType, textureId, null);
	}

	public ClientPartInfo(int[] tints, PartReference part, String subType, String textureId, @Nullable TResourceLocation texture) {
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
		if (info instanceof ClientPartInfo) return (ClientPartInfo) info;
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

	public ClientPartInfo withSubType(Part.SubType value) {
		return new ClientPartInfo(null, getTints().clone(), part, value.id(), textureId, null, false);
	}

	public ClientPartInfo withTexture(Part.PartTexture value) {
		return new ClientPartInfo(null, getTints().clone(), part, subType, value.id(), null, false);
	}

	public ClientPartInfo nextSubType(int direction) {
		return withSubType(cycle(getPart().getSubTypes(), getSubType(), direction));
	}

	public ClientPartInfo nextTexture(int direction) {
		return withTexture(cycle(getSubType().textures(), getPartTexture(), direction));
	}

	private static <T> T cycle(List<T> elements, T current, int direction) {
		final int index = elements.indexOf(current);

		int next = index + direction;

		if (next < 0) next = elements.size() - 1;
		else if (next == elements.size()) next = 0;

		return elements.get(next);
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
		final Part part = getPart();
		if (part == null) return true;
		final Part.SubType subType = part.getSubType(this.subType);
		return subType == null || subType.getTexture(textureId) == null;
	}

	public boolean isPartInvalid() {
		return getPart() == null;
	}

	public boolean isSubTypeInvalid() {
		return getSubType() == null;
	}

	public boolean isTextureInvalid() {
		return getPartTexture() == null;
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
	public TResourceLocation getPartId() {
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
	public TResourceLocation getTexture() {
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
	public void setTexture(@Nullable TResourceLocation texture) {
		final TResourceLocation old = this.texture;

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

	public @Nullable AnimatorStorage getAnimatorStorage() {
		return animatorStorage;
	}

	public void setAnimatorStorage(@Nullable AnimatorStorage storage) {
		animatorStorage = storage;
	}

	public void tickAnimator(TailsEntity entity) {
		if (isInvalid()) return;
		final Part part = getPart();

		if (part.getAnimation() != null && part.getAnimation().isTicking())
			setAnimatorStorage(part.getAnimation().tick(getAnimatorStorage(), entity));
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IPartInfo)) return false;
		final IPartInfo partInfo = (IPartInfo) o;

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
		public TResourceLocation getPartId() { return TailsPlatform.get().newResourceLocation("empty"); }

		@Override
		public String getSubTypeId() { return "empty"; }

		@Override
		public String getTextureId() { return "empty"; }

		@Override
		public int[] getTints() { return Part.DEFAULT_TINTS.clone(); }

		@Override
		public void clearGlTexture() {}

		@Override
		public void setTexture(TResourceLocation texture) {}

		@Override
		public String toString() {
			return "ClientPartInfo.Empty.INSTANCE";
		}
	}
}