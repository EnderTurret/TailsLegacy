package uk.kihira.tails.common;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

class PartsDataDeserializer implements JsonDeserializer<PartsData> {
	@Override
	public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		// Load old data if exists
		if (json.getAsJsonObject().has("partInfos")) {
			final PartsData partsData = new PartsData();
			for (JsonElement e : json.getAsJsonObject().get("partInfos").getAsJsonArray()) {
				final PartInfo info = context.deserialize(e, PartInfo.class);
				partsData.partInfoMap.put(info.partType, info);
			}

			Tails.LOGGER.info("Loading old parts data");
			return partsData;
		}

		// Default serializer. Not the most efficent but works for now
		return new Gson().fromJson(json, typeOfT);
	}
}
