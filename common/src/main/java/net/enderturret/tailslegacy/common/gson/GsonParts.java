/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.part.Parts;

final class GsonParts {

	/**
	 * Determines whether "testing mode" is enabled.
	 * This mode disables logging to avoid class loading FML internals, allowing one to test certain parts of Tails without MC running.
	 */
	static final boolean TESTING = Boolean.getBoolean("tailslegacy.testing");
	static final boolean NO_LOG_IN_TESTING = Boolean.getBoolean("tailslegacy.testing.suppressLog");

	/**
	 * Updates the given json data to the newest part format, if necessary.
	 * @param elem The json data to update.
	 * @return The updated json data.
	 */
	// "We have DFU at home."
	// DFU at home:
	@Internal
	public static JsonElement update(JsonElement elem) {
		if (elem.isJsonObject() && elem.getAsJsonObject().entrySet().size() > 0) {
			JsonObject obj = elem.getAsJsonObject();
			boolean modified = false;
			// Convert old style empty parts to new style empties.
			if (obj.has("hasPart") && !obj.get("hasPart").getAsBoolean()) {
				if (!modified) {
					elem = obj = OldGsonUtils.deepCopy(obj);
					modified = true;
				}

				obj.entrySet().clear(); // Removes all mappings from the object.
				obj.addProperty("id", "tailslegacy:empty");
			}

			// Convert old style parts to new ones.
			if (obj.has("partType") && obj.has("typeid")) {
				if (!modified) {
					elem = obj = OldGsonUtils.deepCopy(obj);
					modified = true;
				}

				final String type = obj.get("partType").getAsString().toLowerCase(Locale.ROOT);
				final int id = obj.get("typeid").getAsInt();
				final TResourceLocation partId = Parts.byLegacyId(type, id);

				obj.remove("partType");
				obj.remove("typeid");
				obj.addProperty("id", partId.toString());

				if (!NO_LOG_IN_TESTING) TailsPlatform.get().logInfo("Remapped part ({}, {}) → {}", type, id, partId);
			}

			if (obj.has("id")) {
				final TResourceLocation oldPartId = TailsPlatform.get().parseResourceLocation(obj.get("id").getAsString());
				final TResourceLocation newPartId = Parts.remapId(oldPartId);

				if (oldPartId != newPartId) {
					if (!modified) {
						elem = obj = OldGsonUtils.deepCopy(obj);
						modified = true;
					}

					obj.addProperty("id", newPartId.toString());

					if (!NO_LOG_IN_TESTING) TailsPlatform.get().logInfo("Remapped part id: {} → {}.", oldPartId, newPartId);
				}

				if (obj.has("subType")) {
					final String oldSubType = obj.get("subType").getAsString();
					final String newSubType = Parts.remapSubTypeId(newPartId, oldSubType);
					if (oldSubType != newSubType) {
						if (!modified) {
							elem = obj = OldGsonUtils.deepCopy(obj);
							modified = true;
						}

						obj.addProperty("subType", newSubType);

						if (!NO_LOG_IN_TESTING) TailsPlatform.get().logInfo("Remapped subtype id: {}.{} → {}.", newPartId, oldSubType, newSubType);
					}
				}

				if (obj.has("textureId")) {
					final String oldTexture = obj.get("textureId").getAsString();
					final String newTexture = Parts.remapTextureId(newPartId, oldTexture);
					if (oldTexture != newTexture) {
						if (!modified) {
							elem = obj = OldGsonUtils.deepCopy(obj);
							modified = true;
						}

						obj.addProperty("textureId", newTexture);

						if (!NO_LOG_IN_TESTING) TailsPlatform.get().logInfo("Remapped texture id: {}.{} → {}.", newPartId, oldTexture, newTexture);
					}
				}

				if (obj.has("subType") && obj.has("textureId")) return elem;

				// Convert old style sub types to new ones.
				if (obj.has("subid")) {
					if (!modified) {
						elem = obj = OldGsonUtils.deepCopy(obj);
						modified = true;
					}

					final int subId = obj.get("subid").getAsInt();
					final String subType = Parts.legacySubType(newPartId, subId);

					obj.remove("subid");
					obj.addProperty("subType", subType);

					if (!NO_LOG_IN_TESTING) TailsPlatform.get().logInfo("Remapped subtype {}.{} → {}", newPartId, subId, subType);
				}

				// Convert old style textures to new ones.
				if (obj.has("textureID")) {
					if (!modified) {
						elem = obj = OldGsonUtils.deepCopy(obj);
						modified = true;
					}

					final int textureId = obj.get("textureID").getAsInt();
					final String texture = Parts.legacyTexture(newPartId, textureId);

					obj.remove("textureID");
					obj.addProperty("textureId", texture);

					if (!NO_LOG_IN_TESTING) TailsPlatform.get().logInfo("Remapped texture {}.{} → {}", newPartId, textureId, texture);
				}
			}
		}

		return elem;
	}

	@Internal
	public static JsonElement updatePartsData(JsonElement elem) {
		if (elem.isJsonObject()) {
			JsonObject obj = elem.getAsJsonObject();
			final int version = obj.has("version") ? obj.get("version").getAsInt() : 0;
			if (version == 2) return elem;

			elem = obj = OldGsonUtils.deepCopy(obj);

			final List<JsonElement> parts = new ArrayList<>();

			if (version == 0 && obj.has("partInfos"))
				for (JsonElement part : obj.get("partInfos").getAsJsonArray()) {
					part = update(part);
					if (!"tailslegacy:empty".equals(part.getAsJsonObject().get("id").getAsString()))
						parts.add(part);
				}

			else if (obj.has("partInfoMap")) {
				// Convert old partInfoMap to new parts list.
				for (Map.Entry<String, JsonElement> entry : obj.get("partInfoMap").getAsJsonObject().entrySet()) {
					JsonElement part = entry.getValue();
					part = update(part);
					if (!"tailslegacy:empty".equals(part.getAsJsonObject().get("id").getAsString()))
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