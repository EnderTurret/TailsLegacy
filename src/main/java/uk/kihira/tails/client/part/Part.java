/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.gui.panel.PartsPanel;
import uk.kihira.tails.client.texture.TripleTintTexture;

/**
 * The client-side representation of a part.
 * @author EnderTurret
 */
public final class Part {

	/**
	 * The default tints. Try not to mess with it.
	 */
	public static final int[] DEFAULT_TINTS = { 0xFF0000, 0x00FF00, 0x0000FF };

	protected final ResourceLocation id;
	protected final AttachmentPoint attachment;
	protected final List<SubType> subTypes;
	protected final int[] defaultTints;
	protected final ModelPart model;
	protected final Transformation renderTransforms;
	protected final Transformation previewTransforms;

	public Part(ResourceLocation id, AttachmentPoint attachment, List<SubType> subTypes, @Nullable int[] defaultTints, ModelPart model, Transformation renderTransforms, Transformation previewTransforms) {
		this.id = id;
		this.attachment = attachment;
		this.subTypes = List.copyOf(subTypes);
		this.defaultTints = defaultTints == null ? DEFAULT_TINTS : defaultTints;
		this.model = model;
		this.renderTransforms = renderTransforms;
		this.previewTransforms = previewTransforms;
	}

	public ResourceLocation getId() {
		return id;
	}

	public AttachmentPoint getAttachment() {
		return attachment;
	}

	public List<SubType> getSubTypes() {
		return subTypes;
	}

	public String getTranslationKey() {
		return id.getNamespace() + ".part." + id.getPath();
	}

	public ModelPart getModel() {
		return model;
	}

	public Transformation getRenderTransforms() {
		return renderTransforms;
	}

	public Transformation getPreviewTransforms() {
		return previewTransforms;
	}

	/**
	 * Returns a default {@link ClientPartInfo} for the {@link PartsPanel} to display.
	 * @param subType The sub type of this part.
	 * @return The default {@link ClientPartInfo}.
	 */
	public ClientPartInfo makeDefaultPartInfo(SubType subType) {
		final int[] tints = { 0xFF000000 | defaultTints[0], 0xFF000000 | defaultTints[1], 0xFF000000 | defaultTints[2] };
		final PartTexture texture = subType.textures().get(0);

		return new ClientPartInfo(tints, this, subType, texture);
	}

	@Override
	public String toString() {
		final String tints = defaultTints != DEFAULT_TINTS ? ", defaultTints=[%s, %s, %s]".formatted(Integer.toHexString(defaultTints[0]), Integer.toHexString(defaultTints[1]), Integer.toHexString(defaultTints[2])) : "";
		return "Part[id=" + id + ", attachment=" + attachment + tints + ", subTypes=" + subTypes + "]";
	}

	/**
	 * Represents a "subtype," which is a variant of a part.
	 * @param id The id of the subtype.
	 * @param author The author of the subtype. May be {@code null}.
	 * @param textures A list of textures that apply to the subtype.
	 * @author EnderTurret
	 */
	public static record SubType(String id, @Nullable String author, List<PartTexture> textures) {
		@Override
		public String toString() {
			final String auth = author != null ? ", author=" + author : "";
			return "SubType[id=" + id + auth + ", textures=" + textures + "]";
		}
	}

	/**
	 * Represents a part texture.
	 * @param id The id of the texture.
	 * @param path The path to the texture in a resource pack.
	 * @param author The author of the texture. May be {@code null}.
	 * @param tintingStrategy The {@link TintingStrategy} to use for tinting the texture.
	 * @author EnderTurret
	 */
	public static record PartTexture(String id, String path, @Nullable String author, TintingStrategy tintingStrategy) {
		@Override
		public String toString() {
			final String auth = author != null ? ", author=" + author : "";
			final String strat = tintingStrategy != TintingStrategy.TRIPLE_TINT ? ", tintingStrategy=" + tintingStrategy : "";
			return "PartTexture[id=" + id + ", path=" + path + auth + strat + "]";
		}
	}

	/**
	 * Represents different strategies for tinting textures.
	 * @author EnderTurret
	 */
	public static enum TintingStrategy {

		/**
		 * Apply all three tints to the texture.
		 * @see TripleTintTexture
		 */
		TRIPLE_TINT,
		/**
		 * Apply only the first tint to the texture.
		 * @see RenderSystem#setShaderColor(float, float, float, float)
		 */
		SINGLE_TINT,
		/**
		 * Perform no tinting; leave the texture unchanged.
		 */
		NO_TINT;

		/**
		 * The id of the tinting strategy.
		 */
		public final String id = name().toLowerCase(Locale.ENGLISH);

		/**
		 * Returns the tinting strategy with the given id, or {@code null} if one doesn't exist.
		 * @param id The id of the desired tinting strategy.
		 * @return The tinting strategy.
		 */
		@Nullable
		public static TintingStrategy of(String id) {
			for (TintingStrategy strat : values())
				if (strat.id.equals(id))
					return strat;

			return null;
		}
	}
}