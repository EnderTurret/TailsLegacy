/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.part;

import java.util.HashMap;
import java.util.Map;

import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TResourceLocation;

/**
 * Various utilities for working with parts.
 * @author EnderTurret
 */
public final class Parts {

	/**
	 * Returns the named id of the part at the given index for the given type.
	 * If the index is out of bounds, it's normalized to {@code 0}.
	 * @param partType The part type.
	 * @param index The type id.
	 * @return The part renderer.
	 */
	public static TResourceLocation byLegacyId(String partType, int index) {
		switch (partType) {
			case "tail":
				switch (index) {
					case 0: return id("tail/fluffy_tail");
					case 1: return id("tail/dragon_tail");
					case 2: return id("tail/raccoon_tail");
					case 3: return id("tail/devil_tail");
					case 4: return id("tail/cat_tail");
					case 5: return id("tail/bird_tail");
					case 6: return id("tail/shark_tail");
					case 7: return id("tail/bunny_tail");
					default: return id("tail/fluffy_tail");
				}
			case "ears":
				switch (index) {
					case 0: return id("ears/fox_ears");
					case 1: return id("ears/cat_ears");
					case 2: return id("ears/panda_ears");
					case 3: return id("ears/small_cat_ears");
					case 4: return id("ears/sea_pickle");
					default: return id("ears/fox_ears");
				}
			case "muzzle":
				switch (index) {
					case 0: return id("muzzle/standard_muzzle");
					case 1: return id("muzzle/slim_muzzle");
					case 2: return id("muzzle/thin_muzzle");
					default: return id("muzzle/standard_muzzle");
				}
			case "wings": return id("wings/big_wings");
			default: throw new IllegalArgumentException("Unhandled part type: " + partType);
		}
	}

	private static final Map<String, String> REMAP = new HashMap<>();

	static {
		REMAP.put("fluffy_tail", "tail/fluffy_tail");
		REMAP.put("dragon_tail", "tail/dragon_tail");
		REMAP.put("raccoon_tail", "tail/raccoon_tail");
		REMAP.put("devil_tail", "tail/devil_tail");
		REMAP.put("cat_tail", "tail/cat_tail");
		REMAP.put("bird_tail", "tail/bird_tail");
		REMAP.put("shark_tail", "tail/shark_tail");
		REMAP.put("bunny_tail", "tail/bunny_tail");
		REMAP.put("fox_ears", "ears/fox_ears");
		REMAP.put("cat_ears", "ears/cat_ears");
		REMAP.put("panda_ears", "ears/panda_ears");
		REMAP.put("small_cat_ears", "ears/small_cat_ears");
		REMAP.put("sea_pickle", "ears/sea_pickle");
		REMAP.put("standard_muzzle", "muzzle/standard_muzzle");
		REMAP.put("slim_muzzle", "muzzle/slim_muzzle");
		REMAP.put("thin_muzzle", "muzzle/thin_muzzle");
		REMAP.put("big_wings", "wings/big_wings");

		REMAP.put("ears/head_fin", "ears/small_head_frill");
		REMAP.put("ears/side_fins", "ears/small_side_frills");
	}

	/**
	 * Remaps the given part id, if necessary.
	 * @param id The id to remap.
	 * @return The remapped id.
	 */
	public static TResourceLocation remapId(TResourceLocation id) {
		if ("tails".equals(id.t$getNamespace())) {
			final String newPath = REMAP.get(id.t$getPath());
			if (newPath != null)
				return id.t$withPath(newPath);
		}

		return id;
	}

	/**
	 * Maps a "legacy" subtype to the corresponding modern subtype id based on the given part id.
	 * @param id The part id. Necessary to distinguish between different kinds of subtypes.
	 * @param subType The legacy numeric subtype id.
	 * @return The named subtype id.
	 */
	public static String legacySubType(TResourceLocation id, int subType) {
		switch (id.toString()) {
			case "tails:tail/fluffy_tail": return map(subType, "one_tail", "two_tails", "nine_tails");
			case "tails:tail/dragon_tail": return map(subType, "lizard_tail", "dragon_tail");
			case "tails:tail/devil_tail": return map(subType, "with_tip", "no_tip");
			case "tails:ears/fox_ears": return map(subType, "outward", "inward");
			case "tails:muzzle/slim_muzzle": return map(subType, "very_short", "short", "standard", "long", "very_long");
			case "tails:muzzle/standard_muzzle": return map(subType, "very_short", "short", "standard", "long", "very_long");
			case "tails:muzzle/thin_muzzle": return map(subType, "very_short", "short", "standard", "long", "very_long");
			case "tails:wings/big_wings": return map(subType, "large", "small");
			default: return "standard";
		}
	}

	/**
	 * Maps a "legacy" texture id to the corresponding modern texture id based on the given part id.
	 * @param id The part id. Necessary to distinguish between different kinds of textures.
	 * @param texture The legacy numeric texture id.
	 * @return The named texture id.
	 */
	public static String legacyTexture(TResourceLocation id, int texture) {
		switch (id.toString()) {
			case "tails:tail/dragon_tail": return map(texture, "standard", "striped");
			case "tails:tail/cat_tail": return map(texture, "tabby", "tiger");
			case "tails:muzzle/slim_muzzle": return map(texture, "standard", "alt");
			case "tails:muzzle/standard_muzzle": return map(texture, "standard", "alt");
			case "tails:muzzle/thin_muzzle": return map(texture, "standard", "alt");
			case "tails:wings/big_wings": return map(texture, "metal", "dragon", "dragon_boneless");
			default: return "standard";
		}
	}

	/**
	 * Utility method to return the {@code index}th element of a vararg array.
	 * @param index The index of the element in the array. Normalized to 0 if out of bounds.
	 * @param values The elements.
	 * @return The element at {@code index} in {@code values}.
	 */
	private static String map(int index, String... values) {
		if (index < 0 || index > values.length) index = 0;
		return values[index];
	}

	/**
	 * Utility method to build a {@link TResourceLocation} in the Tails namespace.
	 * @param path The path of the {@link TResourceLocation}.
	 * @return The new {@link TResourceLocation}.
	 */
	private static TResourceLocation id(String path) {
		return TailsPlatform.get().newResourceLocation(path);
	}
}