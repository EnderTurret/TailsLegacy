/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.part;

import java.util.LinkedHashMap;
import java.util.Map;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

/**
 * Various utilities for working with parts.
 * @author EnderTurret
 */
public final class Parts {

	/**
	 * Returns the named id of the part at the given index for the given type.
	 * If the index is out of bounds, it's normalized to {@code 0}.
	 * @param partType The part type.
	 * @param partId The type id.
	 * @param subTypeId The subtype id.
	 * @param textureId The texture id.
	 * @return The part renderer.
	 */
	public static TResourceLocation byLegacyId(String partType, int partId, int subTypeId, int textureId) {
		switch (partType) {
			case "tail":
				switch (partId) {
					default:
					case 0: return id("tail/fluffy_tail");
					case 1: return id("tail/dragon_tail");
					case 2: return id("tail/raccoon_tail");
					case 3: return id("tail/devil_tail");
					case 4: return id("tail/cat_tail");
					case 5: return id("tail/bird_tail");
					case 6: return id("tail/shark_tail");
					case 7: return id("tail/bunny_tail");
				}
			case "ears":
				switch (partId) {
					default:
					case 0: return id("head/fox_ears");
					case 1: return id("head/cat_ears");
					case 2: return id("head/panda_ears");
					case 3: return id("head/small_cat_ears");
					case 4: return id("head/sea_pickle");
				}
			case "muzzle":
				switch (partId) {
					default:
					case 0: return id("muzzle/standard_muzzle");
					case 1: return id("muzzle/slim_muzzle");
					case 2: return id("muzzle/thin_muzzle");
				}
			case "wings":
				switch (textureId) {
					default:
					case 0: return id("wings/angel_wings");
					case 1: return id("wings/metal_wings");
					case 2:
					case 3: return id("wings/dragon_wings");
				}
			default: throw new IllegalArgumentException("Unhandled part type: " + partType);
		}
	}

	public static final Map<TResourceLocation, TResourceLocation> REMAP = new LinkedHashMap<>();

	/**
	 * Remaps the given part id, if necessary.
	 * @param id The id to remap.
	 * @return The remapped id.
	 */
	public static TResourceLocation remapId(TResourceLocation id) {
		final TResourceLocation newId = REMAP.get(id);
		if (newId != null) return newId;

		// Try migrating parts with the `tails` namespace to the `tailslegacy` namespace.
		if ("tails".equals(id.t$getNamespace()) && id.t$getPath().contains("/"))
			// Double-wrap the remapping in case this now catches a new migration entry.
			return remapId(TailsPlatform.get().newResourceLocation(id.t$getPath()));

		return id;
	}

	/**
	 * Remaps the given subtype id, if necessary.
	 * @param partId The part id.
	 * @param subType The subtype id to remap.
	 * @return The remapped id.
	 */
	public static String remapSubTypeId(TResourceLocation partId, String subType) {
		// Try migrating "standard" subtypes to "default" for parts in the `tailslegacy` namespace.
		if ("tailslegacy".equals(partId.t$getNamespace()) && subType.equals("standard"))
			return "default";

		return subType;
	}

	/**
	 * Remaps the given texture id, if necessary.
	 * @param partId The part id.
	 * @param texture The texture id to remap.
	 * @return The remapped id.
	 */
	public static String remapTextureId(TResourceLocation partId, String texture) {
		// Try migrating "standard" textures to "default" for parts in the `tailslegacy` namespace.
		if ("tailslegacy".equals(partId.t$getNamespace()) && texture.equals("standard"))
			return "default";

		return texture;
	}

	/**
	 * Maps a "legacy" subtype to the corresponding modern subtype id based on the given part id.
	 * @param id The part id. Necessary to distinguish between different kinds of subtypes.
	 * @param subType The legacy numeric subtype id.
	 * @return The named subtype id.
	 */
	public static String legacySubType(TResourceLocation id, int subType) {
		switch (id.toString()) {
			case "tailslegacy:tail/fluffy_tail": return map(subType, "one_tail", "two_tails", "nine_tails");
			case "tailslegacy:tail/dragon_tail": return map(subType, "lizard_tail", "dragon_tail");
			case "tailslegacy:tail/devil_tail": return map(subType, "with_tip", "no_tip");
			case "tailslegacy:head/fox_ears": return map(subType, "outward", "inward");
			case "tailslegacy:muzzle/slim_muzzle": return map(subType, "very_short", "short", "default", "long", "very_long");
			case "tailslegacy:muzzle/standard_muzzle": return map(subType, "very_short", "short", "default", "long", "very_long");
			case "tailslegacy:muzzle/thin_muzzle": return map(subType, "very_short", "short", "default", "long", "very_long");
			case "tailslegacy:wings/angel_wings":
			case "tailslegacy:wings/metal_wings":
			case "tailslegacy:wings/dragon_wings": return map(subType, "large", "small");
			default: return "default";
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
			case "tailslegacy:tail/dragon_tail": return map(texture, "default", "striped");
			case "tailslegacy:tail/cat_tail": return map(texture, "tabby", "tiger");
			case "tailslegacy:muzzle/slim_muzzle": return map(texture, "default", "alt");
			case "tailslegacy:muzzle/standard_muzzle": return map(texture, "default", "alt");
			case "tailslegacy:muzzle/thin_muzzle": return map(texture, "default", "alt");
			case "tailslegacy:wings/angel_wings":
			case "tailslegacy:wings/metal_wings":
			case "tailslegacy:wings/dragon_wings": return map(texture, /* metal wings */ "default", /* dragon wings */ "default", "alt");
			default: return "default";
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
	 * Utility method to build a {@link TResourceLocation} in the Tails Legacy namespace.
	 * @param path The path of the {@link TResourceLocation}.
	 * @return The new {@link TResourceLocation}.
	 */
	private static TResourceLocation id(String path) {
		return TailsPlatform.get().newResourceLocation(path);
	}
}