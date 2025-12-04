/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartsData;

/**
 * A serializer/deserializer setup for your everyday {@link PartsData} json needs.
 * @author EnderTurret
 */
public class PartsDataSerializer implements JsonDeserializer<PartsData>, JsonSerializer<PartsData> {

	@Override
	public JsonElement serialize(PartsData src, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject ret = new JsonObject();
		final JsonArray parts = new JsonArray();

		for (IPartInfo part : src.getPartInfos())
			if (!part.isEmpty())
				parts.add(context.serialize(part));

		ret.add("parts", parts);
		ret.addProperty("version", src.version);

		return ret;
	}

	@Override
	public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		json = GsonParts.updatePartsData(json);

		final JsonObject obj = json.getAsJsonObject();
		final PartsData ret = new PartsData();

		if (obj.has("parts")) {
			final JsonArray parts = obj.get("parts").getAsJsonArray();

			for (JsonElement entry : parts)
				ret.addPartInfo(context.deserialize(entry, IPartInfo.class));
		}

		return ret;
	}
}