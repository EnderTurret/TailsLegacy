package uk.kihira.tails.common_gson;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public final class TailsGsonHelper {

	public static JsonArray getAsJsonArray(JsonObject json, String memberName) {
		if (json.has(memberName))
			return convertToJsonArray(json.get(memberName), memberName);
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonArray");
    }

	public static JsonObject getAsJsonObject(JsonObject json, String memberName) {
		if (json.has(memberName))
			return convertToJsonObject(json.get(memberName), memberName);
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject");
	}

	public static boolean getAsBoolean(JsonObject json, String memberName) {
		if (json.has(memberName))
			return convertToBoolean(json.get(memberName), memberName);
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
	}

	public static boolean getAsBoolean(JsonObject json, String memberName, boolean fallback) {
		return json.has(memberName) ? convertToBoolean(json.get(memberName), memberName) : fallback;
	}

	public static String getAsString(JsonObject json, String memberName) {
		if (json.has(memberName))
			return convertToString(json.get(memberName), memberName);
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
	}

	//

	public static JsonArray convertToJsonArray(JsonElement json, String memberName) {
		if (json.isJsonArray())
			return json.getAsJsonArray();
		throw new JsonSyntaxException("Expected " + memberName + " to be a JsonArray, was " + getType(json));
    }

	public static JsonObject convertToJsonObject(JsonElement json, String memberName) {
		if (json.isJsonObject())
			return json.getAsJsonObject();
		throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + getType(json));
	}

	public static boolean convertToBoolean(JsonElement json, String memberName) {
		if (json.isJsonPrimitive())
			return json.getAsBoolean();
		throw new JsonSyntaxException("Expected " + memberName + " to be a boolean, was " + getType(json));
	}

	public static String convertToString(JsonElement json, String memberName) {
		if (json.isJsonPrimitive())
			return json.getAsString();
		throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + getType(json));
	}

	public static float convertToFloat(JsonElement json, String memberName) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber())
			return json.getAsFloat();
		throw new JsonSyntaxException("Expected " + memberName + " to be a float, was " + getType(json));
	}

	public static int convertToInt(JsonElement json, String memberName) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber())
			return json.getAsInt();
		throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + getType(json));
	}

	public static String getType(@Nullable JsonElement json) {
		if (json == null) return "null (missing)";
		if (json.isJsonNull()) return "null (json)";

		if (json.isJsonPrimitive()) {
			final JsonPrimitive p = json.getAsJsonPrimitive();
			if (p.isNumber()) return "a number (" + p.getAsNumber() + ")";
			if (p.isBoolean()) return "a boolean (" + p.getAsBoolean() + ")";
			if (p.isString()) return "a string (\"" + p.getAsString() + "\")";
		}

		final String desc = abbreviateMiddle(json.toString(), "...", 10);
		if (json.isJsonArray()) return "an array (" + desc + ")";
		if (json.isJsonObject()) return "an object (" + desc + ")";

		return desc;
	}

	private static String abbreviateMiddle(final String str, final String middle, final int length) {
		if (str.isEmpty() || middle.isEmpty() || length >= str.length() || length < middle.length()+2) {
			return str;
		}

		final int targetString = length - middle.length();
		final int startOffset = targetString / 2 + targetString % 2;
		final int endOffset = str.length() - targetString / 2;

		return str.substring(0, startOffset) + middle + str.substring(endOffset);
    }
}