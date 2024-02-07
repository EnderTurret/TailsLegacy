/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;

/**
 * Various utilities for working with parts.
 * @author EnderTurret
 */
public final class Parts {

	/**
	 * Determines whether "testing mode" is enabled.
	 * This mode disables logging to avoid class loading FML internals, allowing one to test certain parts of Tails without MC running.
	 */
	static final boolean TESTING = Boolean.getBoolean("tails.testing");

	/**
	 * Returns the named id of the part at the given index for the given type.
	 * If the index is out of bounds, it's normalized to {@code 0}.
	 * @param partType The part type.
	 * @param index The type id.
	 * @return The part renderer.
	 */
	public static ResourceLocation byLegacyId(String partType, int index) {
		return switch (partType) {
		case "tail" -> {
			yield switch (index) {
			case 0 -> id("tail/fluffy_tail");
			case 1 -> id("tail/dragon_tail");
			case 2 -> id("tail/raccoon_tail");
			case 3 -> id("tail/devil_tail");
			case 4 -> id("tail/cat_tail");
			case 5 -> id("tail/bird_tail");
			case 6 -> id("tail/shark_tail");
			case 7 -> id("tail/bunny_tail");
			default -> id("tail/fluffy_tail");
			};
		}
		case "ears" -> {
			yield switch (index) {
			case 0 -> id("ears/fox_ears");
			case 1 -> id("ears/cat_ears");
			case 2 -> id("ears/panda_ears");
			case 3 -> id("ears/small_cat_ears");
			case 4 -> id("ears/sea_pickle");
			default -> id("ears/fox_ears");
			};
		}
		case "muzzle" -> {
			yield switch (index) {
			case 0 -> id("muzzle/standard_muzzle");
			case 1 -> id("muzzle/slim_muzzle");
			case 2 -> id("muzzle/thin_muzzle");
			default -> id("muzzle/standard_muzzle");
			};
		}
		case "wings" -> id("wings/big_wings");
		default -> throw new IllegalArgumentException("Unhandled part type: " + partType);
		};
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
	}

	/**
	 * Remaps the given part id, if necessary.
	 * @param id The id to remap.
	 * @return The remapped id.
	 */
	public static ResourceLocation remapId(ResourceLocation id) {
		if ("tails".equals(id.getNamespace())) {
			final String newPath = REMAP.get(id.getPath());
			if (newPath != null)
				return new ResourceLocation(id.getNamespace(), newPath);
		}

		return id;
	}

	/**
	 * Maps a "legacy" subtype to the corresponding modern subtype id based on the given part id.
	 * @param id The part id. Necessary to distinguish between different kinds of subtypes.
	 * @param subType The legacy numeric subtype id.
	 * @return The named subtype id.
	 */
	public static String legacySubType(ResourceLocation id, int subType) {
		return switch (id.toString()) {
		case "tails:tail/fluffy_tail" -> map(subType, "one_tail", "two_tails", "nine_tails");
		case "tails:tail/dragon_tail" -> map(subType, "lizard_tail", "dragon_tail");
		case "tails:tail/devil_tail" -> map(subType, "with_tip", "no_tip");
		case "tails:ears/fox_ears" -> map(subType, "outward", "inward");
		case "tails:muzzle/slim_muzzle" -> map(subType, "very_short", "short", "standard", "long", "very_long");
		case "tails:muzzle/standard_muzzle" -> map(subType, "very_short", "short", "standard", "long", "very_long");
		case "tails:muzzle/thin_muzzle" -> map(subType, "very_short", "short", "standard", "long", "very_long");
		case "tails:wings/big_wings" -> map(subType, "large", "small");
		default -> "standard";
		};
	}

	/**
	 * Maps a "legacy" texture id to the corresponding modern texture id based on the given part id.
	 * @param id The part id. Necessary to distinguish between different kinds of textures.
	 * @param texture The legacy numeric texture id.
	 * @return The named texture id.
	 */
	public static String legacyTexture(ResourceLocation id, int texture) {
		return switch (id.toString()) {
		case "tails:tail/dragon_tail" -> map(texture, "standard", "striped");
		case "tails:tail/cat_tail" -> map(texture, "tabby", "tiger");
		case "tails:muzzle/slim_muzzle" -> map(texture, "standard", "alt");
		case "tails:muzzle/standard_muzzle" -> map(texture, "standard", "alt");
		case "tails:muzzle/thin_muzzle" -> map(texture, "standard", "alt");
		case "tails:wings/big_wings" -> map(texture, "metal", "dragon", "dragon_boneless");
		default -> "standard";
		};
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
	 * Utility method to build a {@link ResourceLocation} in the Tails namespace.
	 * @param path The path of the {@link ResourceLocation}.
	 * @return The new {@link ResourceLocation}.
	 */
	private static ResourceLocation id(String path) {
		return new ResourceLocation(Tails.MOD_ID, path);
	}

	/**
	 * Updates the given json data to the newest part format, if necessary.
	 * @param elem The json data to update.
	 * @return The updated json data.
	 */
	// "We have DFU at home."
	// DFU at home:
	@Internal
	public static JsonElement update(JsonElement elem) {
		if (elem instanceof JsonObject obj && obj.size() > 0) {
			// Convert old style empty parts to new style empties.
			if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean()) {
				obj.keySet().clear(); // Removes all mappings from the object.
				obj.addProperty("id", "tails:empty");
			}

			// Convert old style parts to new ones.
			if (obj.has("partType") && obj.has("typeid")) {
				final String type = obj.get("partType").getAsString().toLowerCase(Locale.ROOT);
				final int id = obj.get("typeid").getAsInt();
				final ResourceLocation partId = byLegacyId(type, id);

				obj.remove("partType");
				obj.remove("typeid");
				obj.addProperty("id", partId.toString());

				if (!TESTING) Tails.LOGGER.info("Remapped part ({}, {}) → {}", type, id, partId);
			}

			if (obj.has("id")) {
				final ResourceLocation oldPartId = new ResourceLocation(obj.get("id").getAsString());
				final ResourceLocation newPartId = Parts.remapId(oldPartId);

				if (oldPartId != newPartId) {
					obj.addProperty("id", newPartId.toString());

					if (!TESTING) Tails.LOGGER.info("Remapped part id: {} → {}.", oldPartId, newPartId);
				}

				if (obj.has("subType") && obj.has("textureId")) return elem;

				// Convert old style sub types to new ones.
				if (obj.has("subid")) {
					final int subId = obj.get("subid").getAsInt();
					final String subType = legacySubType(newPartId, subId);

					obj.remove("subid");
					obj.addProperty("subType", subType);

					if (!TESTING) Tails.LOGGER.info("Remapped subtype {} → {}", subId, subType);
				}

				// Convert old style textures to new ones.
				if (obj.has("textureID")) {
					final int textureId = obj.get("textureID").getAsInt();
					final String texture = legacyTexture(newPartId, textureId);

					obj.remove("textureID");
					obj.addProperty("textureId", texture);

					if (!TESTING) Tails.LOGGER.info("Remapped texture {} → {}", textureId, texture);
				}
			}
		}

		return elem;
	}

	@Internal
	public static JsonElement updatePartsData(JsonElement elem) {
		if (elem instanceof JsonObject obj) {
			final int version = obj.has("version") ? obj.get("version").getAsInt() : 0;
			if (version == 2) return elem;

			final List<JsonElement> parts = new ArrayList<>();

			if (version == 0 && obj.has("partInfos"))
				for (JsonElement part : obj.get("partInfos").getAsJsonArray()) {
					update(part);
					if (!"tails:empty".equals(part.getAsJsonObject().get("id").getAsString()))
						parts.add(part);
				}

			else if (obj.has("partInfoMap")) {
				// Convert old partInfoMap to new parts list.
				for (Map.Entry<String, JsonElement> entry : obj.get("partInfoMap").getAsJsonObject().entrySet()) {
					final JsonElement part = entry.getValue();
					update(part);
					if (!"tails:empty".equals(part.getAsJsonObject().get("id").getAsString()))
						parts.add(part);
				}
			}

			final JsonArray partsArray = new JsonArray();
			for (JsonElement p : parts) partsArray.add(p);

			obj.addProperty("version", 2);
			obj.add("parts", partsArray);
		}

		return elem;
	}
}