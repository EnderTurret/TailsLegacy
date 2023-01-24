/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Holds a {@link IPartInfo} for every part type.
 */
public class PartsData {

	/**
	 * The singleton empty {@link PartsData}.
	 */
	public static final PartsData EMPTY = new PartsData() {
		@Override
		public void addPartInfo(IPartInfo partInfo) {}
		@Override
		public void clearTextures() {}
		@Override
		public String toString() { return "PartsData#EMPTY"; }
		@Override
		public boolean isEmpty() { return true; }
	};

	protected final Set<IPartInfo> parts = new LinkedHashSet<>();

	/**
	 * The version.<br>
	 * 1 is the current version.
	 */
	private final int version = 2;

	public PartsData() {}

	public PartsData(Set<IPartInfo> parts) {
		this();
		this.parts.addAll(parts);
	}

	protected IPartInfo empty() {
		return IPartInfo.empty();
	}

	public boolean isEmpty() {
		return false;
	}

	/**
	 * Adds the given part info.
	 * @param partInfo The part info.
	 */
	public void addPartInfo(IPartInfo partInfo) {
		parts.add(Objects.requireNonNull(partInfo, "partInfo"));
	}

	public Set<IPartInfo> getPartInfos() {
		return Collections.unmodifiableSet(parts);
	}

	/**
	 * Clears all textures from each {@link IPartInfo}.
	 */
	public void clearTextures() {
		for (IPartInfo partInfo : parts)
			if (partInfo != null) partInfo.clearGlTexture();
	}

	/**
	 * Returns a copy of this {@link PartsData}.
	 * @return The copy.
	 */
	public PartsData deepCopy() {
		final Set<IPartInfo> data = new LinkedHashSet<>();
		for (IPartInfo info : parts)
			data.add(info.clone());

		return new PartsData(data);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PartsData partsData)) return false;

		return parts.equals(partsData.parts);
	}

	@Override
	public int hashCode() {
		return parts.hashCode();
	}

	@Override
	public String toString() {
		return "PartsData{" + parts.stream()
				.filter(e -> !e.isEmpty())
				.map(e -> e.toString())
				.collect(Collectors.joining(", ")) + '}';
	}

	/**
	 * A serializer/deserializer setup for your everyday {@link PartsData} json needs.
	 * @author EnderTurret
	 */
	public static class Serializer implements JsonDeserializer<PartsData>, JsonSerializer<PartsData> {

		@Override
		public JsonElement serialize(PartsData src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject ret = new JsonObject();
			final JsonArray parts = new JsonArray();

			for (IPartInfo part : src.parts)
				if (!part.isEmpty())
					parts.add(context.serialize(part));

			ret.add("parts", parts);
			ret.addProperty("version", src.version);

			return ret;
		}

		@Override
		public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			json = json.deepCopy();
			Parts.updatePartsData(json);

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
}