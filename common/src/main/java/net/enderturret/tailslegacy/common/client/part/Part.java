/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.animation.ModelAnimator;

/**
 * The client-side representation of a part.
 * @author EnderTurret
 */
public final class Part {

	/**
	 * The default tints. Try not to mess with it.
	 */
	public static final int[] DEFAULT_TINTS = { 0xFF0000, 0x00FF00, 0x0000FF };

	protected final TResourceLocation id;
	protected final AttachmentPoint attachment;
	protected final List<SubType> subTypes;
	protected final int[] defaultTints;
	protected final ModelPredicate allowArrows;
	protected final @Nullable TailsModelPart model;
	protected final @Nullable ModelAnimator animation;
	protected final Transformation renderTransforms;
	protected final Transformation previewTransforms;

	public Part(TResourceLocation id, AttachmentPoint attachment, List<SubType> subTypes, @Nullable int[] defaultTints, ModelPredicate allowArrows, @Nullable TailsModelPart model, @Nullable ModelAnimator animation, Transformation renderTransforms, Transformation previewTransforms) {
		this.id = id;
		this.attachment = attachment;
		this.subTypes = Collections.unmodifiableList(new ArrayList<>(subTypes));
		this.defaultTints = defaultTints == null ? DEFAULT_TINTS : defaultTints;
		this.allowArrows = allowArrows;
		this.model = model;
		this.animation = animation;
		this.renderTransforms = renderTransforms;
		this.previewTransforms = previewTransforms;
	}

	public TResourceLocation getId() {
		return id;
	}

	public AttachmentPoint getAttachment() {
		return attachment;
	}

	public List<SubType> getSubTypes() {
		return subTypes;
	}

	public String getTranslationKey() {
		return id.t$getNamespace() + ".part." + id.t$getPath();
	}

	public ModelPredicate allowArrows() {
		return allowArrows;
	}

	public @Nullable TailsModelPart getModel() {
		return model;
	}

	public @Nullable ModelAnimator getAnimation() {
		return animation;
	}

	public Transformation getRenderTransforms() {
		return renderTransforms;
	}

	public Transformation getPreviewTransforms() {
		return previewTransforms;
	}

	@Nullable
	public SubType getSubType(String id) {
		for (SubType type : subTypes)
			if (type.id().equals(id))
				return type;

		return null;
	}

	/**
	 * Returns a default {@link ClientPartInfo} for the {@code PartsPanel} to display.
	 * @param subType The sub type of this part.
	 * @return The default {@link ClientPartInfo}.
	 */
	public ClientPartInfo makeDefaultPartInfo(SubType subType) {
		final int[] tints = { 0xFF000000 | defaultTints[0], 0xFF000000 | defaultTints[1], 0xFF000000 | defaultTints[2] };
		final PartTexture texture = subType.textures().get(0);

		return new ClientPartInfo(tints, PartRegistry.reference(id), subType.id(), texture.id());
	}

	@Override
	public String toString() {
		final String tints = defaultTints != DEFAULT_TINTS ? String.format(", defaultTints=[%s, %s, %s]", Integer.toHexString(defaultTints[0]), Integer.toHexString(defaultTints[1]), Integer.toHexString(defaultTints[2])) : "";
		return "Part[id=" + id + ", attachment=" + attachment + tints + ", subTypes=" + subTypes + "]";
	}
}