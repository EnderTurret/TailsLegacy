package uk.kihira.tails.common2.gson;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class OldGsonUtils {

	@SuppressWarnings("unchecked")
	public static <T extends JsonElement> T deepCopy(T input) {
		if (input == JsonNull.INSTANCE || input instanceof JsonPrimitive) return input;

		if (input instanceof JsonArray) {
			final JsonArray old = (JsonArray) input;
			final JsonArray ret = new JsonArray();

			for (JsonElement elem : old)
				ret.add(deepCopy(elem));

			return (T) ret;
		}

		if (input instanceof JsonObject) {
			final JsonObject old = (JsonObject) input;
			final JsonObject ret = new JsonObject();

			for (Map.Entry<String, JsonElement> entry : old.entrySet())
				ret.add(entry.getKey(), deepCopy(entry.getValue()));

			return (T) ret;
		}

		throw new IllegalArgumentException("Unknown json element type " + input);
	}
}