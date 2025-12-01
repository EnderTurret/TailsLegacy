package uk.kihira.tails.common_gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.part.Parts;

final class GsonParts {

	/**
	 * Determines whether "testing mode" is enabled.
	 * This mode disables logging to avoid class loading FML internals, allowing one to test certain parts of Tails without MC running.
	 */
	static final boolean TESTING = Boolean.getBoolean("tails.testing");

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
			boolean modified = false;
			// Convert old style empty parts to new style empties.
			if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean()) {
				if (!modified) {
					elem = obj = obj.deepCopy();
					modified = true;
				}

				obj.keySet().clear(); // Removes all mappings from the object.
				obj.addProperty("id", "tails:empty");
			}

			// Convert old style parts to new ones.
			if (obj.has("partType") && obj.has("typeid")) {
				if (!modified) {
					elem = obj = obj.deepCopy();
					modified = true;
				}

				final String type = obj.get("partType").getAsString().toLowerCase(Locale.ROOT);
				final int id = obj.get("typeid").getAsInt();
				final TResourceLocation partId = Parts.byLegacyId(type, id);

				obj.remove("partType");
				obj.remove("typeid");
				obj.addProperty("id", partId.toString());

				if (!TESTING) Tails.LOGGER.info("Remapped part ({}, {}) → {}", type, id, partId);
			}

			if (obj.has("id")) {
				final TResourceLocation oldPartId = TailsPlatform.get().parseResourceLocation(obj.get("id").getAsString());
				final TResourceLocation newPartId = Parts.remapId(oldPartId);

				if (oldPartId != newPartId) {
					if (!modified) {
						elem = obj = obj.deepCopy();
						modified = true;
					}

					obj.addProperty("id", newPartId.toString());

					if (!TESTING) Tails.LOGGER.info("Remapped part id: {} → {}.", oldPartId, newPartId);
				}

				if (obj.has("subType") && obj.has("textureId")) return elem;

				// Convert old style sub types to new ones.
				if (obj.has("subid")) {
					if (!modified) {
						elem = obj = obj.deepCopy();
						modified = true;
					}

					final int subId = obj.get("subid").getAsInt();
					final String subType = Parts.legacySubType(newPartId, subId);

					obj.remove("subid");
					obj.addProperty("subType", subType);

					if (!TESTING) Tails.LOGGER.info("Remapped subtype {} → {}", subId, subType);
				}

				// Convert old style textures to new ones.
				if (obj.has("textureID")) {
					if (!modified) {
						elem = obj = obj.deepCopy();
						modified = true;
					}

					final int textureId = obj.get("textureID").getAsInt();
					final String texture = Parts.legacyTexture(newPartId, textureId);

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

			elem = obj = obj.deepCopy();

			final List<JsonElement> parts = new ArrayList<>();

			if (version == 0 && obj.has("partInfos"))
				for (JsonElement part : obj.get("partInfos").getAsJsonArray()) {
					part = update(part);
					if (!"tails:empty".equals(part.getAsJsonObject().get("id").getAsString()))
						parts.add(part);
				}

			else if (obj.has("partInfoMap")) {
				// Convert old partInfoMap to new parts list.
				for (Map.Entry<String, JsonElement> entry : obj.get("partInfoMap").getAsJsonObject().entrySet()) {
					JsonElement part = entry.getValue();
					part = update(part);
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