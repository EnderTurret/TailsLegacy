/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a part texture.
 * @author EnderTurret
 */
public final class PartTexture {

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