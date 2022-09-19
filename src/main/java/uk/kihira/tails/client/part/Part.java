/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.gui.panel.PartsPanel;
import uk.kihira.tails.common.part.PartType;

public final class Part {

	private static final int[] DEFAULT_TINTS = { 0xFF0000, 0x00FF00, 0x0000FF };

	protected final ResourceLocation id;
	protected final PartType type;
	protected final List<SubType> subTypes;
	protected final int[] defaultTints;

	public Part(ResourceLocation id, PartType type, List<SubType> subTypes, @Nullable int[] defaultTints) {
		this.id = id;
		this.type = type;
		this.subTypes = List.copyOf(subTypes);
		this.defaultTints = defaultTints == null ? DEFAULT_TINTS : defaultTints;
	}

	public ResourceLocation getId() {
		return id;
	}

	public PartType getType() {
		return type;
	}

	public List<SubType> getSubTypes() {
		return subTypes;
	}

	public String getTranslationKey() {
		return id.getNamespace() + ".part." + id.getPath();
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
		return "Part[id=" + id + ", type=" + type + tints + ", subTypes=" + subTypes + "]";
	}

	public static record SubType(String id, @Nullable String author, List<PartTexture> textures) {}

	public static record PartTexture(String id, String path, @Nullable String author, TintingStrategy tintingStrategy) {}

	public static enum TintingStrategy {

		TRIPLE_TINT,
		SINGLE_TINT,
		NO_TINT;

		public final String id = name().toLowerCase(Locale.ENGLISH);

		public static TintingStrategy of(String id) {
			for (TintingStrategy strat : values())
				if (strat.id.equals(id))
					return strat;

			return null;
		}
	}
}