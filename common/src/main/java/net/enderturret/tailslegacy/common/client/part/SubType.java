/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a "subtype," which is a variant of a part.
 * @author EnderTurret
 */
public final class SubType {

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
			if (tex.id().equals(id))
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