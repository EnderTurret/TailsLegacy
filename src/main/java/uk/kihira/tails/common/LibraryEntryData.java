/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.GsonHelper;

import uk.kihira.tails.common.part.PartsData;

/**
 * Represents a single library entry.
 */
@Internal
public final class LibraryEntryData {

	public String entryName = "";
	public final long creationDate;
	public final UUID creatorUUID;
	/**
	 * Name is used purely for display purposes.
	 */
	public String creatorName;
	public boolean favourite;
	public final PartsData partsData;

	public LibraryEntryData(String entryName, long creationDate, UUID creatorUUID, String creatorName, boolean favourite, PartsData partsData) {
		this.entryName = entryName;
		this.creationDate = creationDate;
		this.creatorUUID = creatorUUID;
		this.creatorName = creatorName;
		this.favourite = favourite;
		this.partsData = partsData;
	}

	public LibraryEntryData(UUID creatorUUID, String creatorName, String name, PartsData partsData) {
		this(name, Instant.now().toEpochMilli(), creatorUUID, creatorName, false, partsData);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LibraryEntryData data)) return false;

		return creationDate == data.creationDate && favourite == data.favourite && creatorUUID.equals(data.creatorUUID)
				&& Objects.equals(entryName, data.entryName) && Objects.equals(partsData, data.partsData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(creationDate, favourite, creatorUUID, entryName, partsData);
	}

	@Internal
	public static final class Serializer implements JsonDeserializer<LibraryEntryData>, JsonSerializer<LibraryEntryData> {

		@Override
		public LibraryEntryData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject entry = GsonHelper.convertToJsonObject(json, "root");
			return new LibraryEntryData(
					GsonHelper.getAsString(entry, "entryName"),
					GsonHelper.getAsLong(entry, "creationDate"),
					UUID.fromString(GsonHelper.getAsString(entry, "creatorUUID")),
					GsonHelper.getAsString(entry, "creatorName"),
					GsonHelper.getAsBoolean(entry, "favourite"),
					context.deserialize(GsonHelper.getAsJsonObject(entry, "partsData"), PartsData.class)
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
}
