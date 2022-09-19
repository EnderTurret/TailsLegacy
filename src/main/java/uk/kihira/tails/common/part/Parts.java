package uk.kihira.tails.common.part;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;

public final class Parts {

	/**
	 * Returns the named id of the part at the given index for the given type.<br>
	 * If the index is out of bounds, it's normalized to {@code 0}.
	 * @param partType The part type.
	 * @param index The type id.
	 * @return The part renderer.
	 */
	public static ResourceLocation byLegacyId(PartType partType, int index) {
		return switch (partType) {
		case TAIL -> {
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
		case EARS -> {
			yield switch (index) {
			case 0 -> id("ears/fox_ears");
			case 1 -> id("ears/cat_ears");
			case 2 -> id("ears/panda_ears");
			case 3 -> id("ears/small_cat_ears");
			case 4 -> id("ears/sea_pickle");
			default -> id("ears/fox_ears");
			};
		}
		case MUZZLE -> {
			yield switch (index) {
			case 0 -> id("muzzle/standard_muzzle");
			case 1 -> id("muzzle/slim_muzzle");
			case 2 -> id("muzzle/thin_muzzle");
			default -> id("muzzle/standard_muzzle");
			};
		}
		case WINGS -> id("wings/big_wings");
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

	public static ResourceLocation remapId(ResourceLocation id) {
		if (id.getNamespace().equals("tails")) {
			final String newPath = REMAP.get(id.getPath());
			if (newPath != null)
				return new ResourceLocation(id.getNamespace(), newPath);
		}

		return id;
	}

	public static String legacySubType(ResourceLocation id, int subType) {
		return switch (id.toString()) {
		case "tails:tail/fluffy_tail" -> map(subType, "one_tail", "two_tails", "nine_tails");
		case "tails:tail/dragon_tail" -> map(subType, "lizard_tail", "dragon_tail");
		case "tails:tail/devil_tail" -> map(subType, "with_tip", "no_tip");
		case "tails:ears/fox_ears" -> map(subType, "outward", "inward");
		case "tails:muzzle/slim_muzzle" -> map(subType, "very_short", "short", "standard", "long", "very_long");
		case "tails:muzzle/standard_muzzle" -> map(subType, "very_short", "short", "standard", "long", "very_long");
		case "tails:muzzle/thin_muzzle" -> map(subType, "very_short", "short", "standard", "long", "very_long");
		case "tails:wings/big_wings" -> map(subType, "big_wings", "small_wings");
		default -> "standard";
		};
	}

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

	private static String map(int index, String... values) {
		if (index < 0 || index > values.length) index = 0;
		return values[index];
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(Tails.MOD_ID, path);
	}

	public static JsonElement update(JsonElement elem) {
		if (elem instanceof JsonObject obj) {
			if (obj.has("id") && obj.has("subType") && obj.has("textureId")) return elem;

			// Convert old style empty parts to new style empties.
			if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean()) {
				obj.keySet().clear(); // Removes all mappings from the object.
				obj.addProperty("id", "tails:empty");
			}

			// Convert old style parts to new ones.
			if (obj.has("partType") && obj.has("typeid")) {
				final PartType type = PartType.forId(obj.get("partType").getAsString().toLowerCase(Locale.ROOT));
				final int id = obj.get("typeid").getAsInt();
				final ResourceLocation partId = byLegacyId(type, id);

				obj.remove("partType");
				obj.remove("typeid");
				obj.addProperty("id", partId.toString());
				Tails.LOGGER.info("Remapped part ({}, {}) → {}", type.getId(), id, partId);
			}

			final ResourceLocation partId = ResourceLocation.tryParse(obj.get("id").getAsString());

			// Convert old style sub types to new ones.
			if (obj.has("subid")) {
				final int subId = obj.get("subid").getAsInt();
				final String subType = legacySubType(partId, subId);
				obj.remove("subid");
				obj.addProperty("subType", subType);
				Tails.LOGGER.info("Remapped sub type {} → {}", subId, subType);
			}

			// Convert old style textures to new ones.
			if (obj.has("textureID")) {
				final int textureId = obj.get("textureID").getAsInt();
				final String texture = legacyTexture(partId, textureId);
				obj.remove("textureID");
				obj.addProperty("textureId", texture);
				Tails.LOGGER.info("Remapped texture {} → {}", textureId, texture);
			}
		}

		return elem;
	}
}