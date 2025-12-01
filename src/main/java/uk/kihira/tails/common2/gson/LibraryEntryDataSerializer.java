package uk.kihira.tails.common2.gson;

import java.lang.reflect.Type;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.part.PartsData;

@Internal
public final class LibraryEntryDataSerializer implements JsonDeserializer<LibraryEntryData>, JsonSerializer<LibraryEntryData> {

	@Override
	public LibraryEntryData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject entry = TailsGsonHelper.convertToJsonObject(json, "root");
		return new LibraryEntryData(
				TailsGsonHelper.getAsString(entry, "entryName"),
				TailsGsonHelper.getAsLong(entry, "creationDate"),
				UUID.fromString(TailsGsonHelper.getAsString(entry, "creatorUUID")),
				TailsGsonHelper.getAsString(entry, "creatorName"),
				TailsGsonHelper.getAsBoolean(entry, "favourite"),
				context.deserialize(TailsGsonHelper.getAsJsonObject(entry, "partsData"), PartsData.class)
				);
	}

	@Override
	public JsonElement serialize(LibraryEntryData src, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject ret = new JsonObject();

		ret.addProperty("entryName", src.entryName);
		ret.addProperty("creationDate", src.creationDate);
		ret.addProperty("creatorUUID", src.creatorUUID.toString());
		ret.addProperty("creatorName", src.creatorName);
		ret.addProperty("favourite", src.favourite);
		ret.add("partsData", context.serialize(src.partsData));

		return ret;
	}
}