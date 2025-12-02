/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.duck.TailsModelPart;

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
	protected final boolean allowArrows;
	protected final TailsModelPart model;
	protected final Transformation renderTransforms;
	protected final Transformation previewTransforms;

	public Part(TResourceLocation id, AttachmentPoint attachment, List<SubType> subTypes, @Nullable int[] defaultTints, boolean allowArrows, TailsModelPart model, Transformation renderTransforms, Transformation previewTransforms) {
		this.id = id;
		this.attachment = attachment;
		this.subTypes = Collections.unmodifiableList(new ArrayList<>(subTypes));
		this.defaultTints = defaultTints == null ? DEFAULT_TINTS : defaultTints;
		this.allowArrows = allowArrows;
		this.model = model;
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

	public boolean allowArrows() {
		return allowArrows;
	}

	public TailsModelPart getModel() {
		return model;
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
			if (type.id.equals(id))
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

		return new ClientPartInfo(tints, PartRegistry.reference(id), subType.id, texture.id);
	}

	@Override
	public String toString() {
		final String tints = defaultTints != DEFAULT_TINTS ? String.format(", defaultTints=[%s, %s, %s]", Integer.toHexString(defaultTints[0]), Integer.toHexString(defaultTints[1]), Integer.toHexString(defaultTints[2])) : "";
		return "Part[id=" + id + ", attachment=" + attachment + tints + ", subTypes=" + subTypes + "]";
	}

	/**
	 * Represents a "subtype," which is a variant of a part.
	 * @author EnderTurret
	 */
	public static final class SubType {

		private final String id;
		private final @Nullable String author;
		private final Transformation renderTransforms;
		private final List<PartPath> hideParts;
		private final List<PartPath> showParts;
		private final List<PartTexture> textures;

		/**
		 * Constructs a new {@code SubType}.
		 * @param id The id of the subtype.
		 * @param author The author of the subtype. May be {@code null}.
		 * @param renderTransforms The render transformation.
		 * @param hideParts Parts that should be hidden before render.
		 * @param showParts Parts that should be shown before render.
		 * @param textures A list of textures that apply to the subtype.
		 */
		public SubType(String id, @Nullable String author, Transformation renderTransforms, List<PartPath> hideParts, List<PartPath> showParts, List<PartTexture> textures) {
			this.id = id;
			this.author = author;
			this.renderTransforms = renderTransforms;
			this.hideParts = hideParts;
			this.showParts = showParts;
			this.textures = textures;
		}

		public String id() {
			return id;
		}

		public @Nullable String author() {
			return author;
		}

		public Transformation renderTransforms() {
			return renderTransforms;
		}

		public List<PartPath> hideParts() {
			return hideParts;
		}

		public List<PartPath> showParts() {
			return showParts;
		}

		public List<PartTexture> textures() {
			return textures;
		}

		@Nullable
		public PartTexture getTexture(String id) {
			for (PartTexture tex : textures)
				if (tex.id.equals(id))
					return tex;

			return null;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof SubType)) return false;
			final SubType s = (SubType) obj;
			return id.equals(s.id) && Objects.equals(author, s.author) && renderTransforms.equals(s.renderTransforms) && hideParts.equals(s.hideParts)
					&& showParts.equals(s.showParts) && textures.equals(s.textures);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, author, renderTransforms, hideParts, showParts, textures);
		}

		@Override
		public String toString() {
			final String auth = author != null ? ", author=" + author : "";
			return "SubType[id=" + id + auth + ", textures=" + textures + "]";
		}
	}

	/**
	 * Represents a part texture.
	 * @author EnderTurret
	 */
	public static final class PartTexture {

		private final String id;
		private final String path;
		private final @Nullable String author;
		private final TintingStrategy tintingStrategy;

		/**
		 * Constructs a new {@code PartTexture}.
		 * @param id The id of the texture.
		 * @param path The path to the texture in a resource pack.
		 * @param author The author of the texture. May be {@code null}.
		 * @param tintingStrategy The {@link TintingStrategy} to use for tinting the texture.
		 */
		public PartTexture(String id, String path, @Nullable String author, TintingStrategy tintingStrategy) {
			this.id = id;
			this.path = path;
			this.author = author;
			this.tintingStrategy = tintingStrategy;
		}

		public String id() {
			return id;
		}

		public String path() {
			return path;
		}

		public @Nullable String author() {
			return author;
		}

		public TintingStrategy tintingStrategy() {
			return tintingStrategy;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof PartTexture)) return false;
			final PartTexture t = (PartTexture) obj;
			return id.equals(t.id) && path.equals(t.path) && Objects.equals(author, t.author) && tintingStrategy == t.tintingStrategy;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, path, author, tintingStrategy);
		}

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
		 */
		TRIPLE_TINT,
		/**
		 * Apply only the first tint to the texture.
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