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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
		public IPartInfo getPartInfo(String partType) { return empty(); }
		@Override
		public void setPartInfo(String partType, IPartInfo partInfo) {}
		@Override
		public void clearTextures() {}
		@Override
		public boolean hasPartInfo(String partType) { return false; }
		@Override
		public String toString() { return "PartsData#EMPTY"; }
		@Override
		public boolean isEmpty() { return true; }
	};

	private final Map<String, IPartInfo> partInfoMap = new HashMap<>();

	/**
	 * The version.<br>
	 * 1 is the current version.
	 */
	private final int version = 1;

	public PartsData() {}

	public PartsData(Map<String, IPartInfo> partData) {
		this();
		partInfoMap.putAll(partData);
	}

	protected IPartInfo empty() {
		return IPartInfo.empty();
	}

	public boolean isEmpty() {
		return false;
	}

	/**
	 * Sets the {@link IPartInfo} for the given type as the given part info.
	 * @param partType The part type to set the part info as.
	 * @param partInfo The part info.
	 */
	public void setPartInfo(String partType, IPartInfo partInfo) {
		Objects.requireNonNull(partType, "partType");
		Objects.requireNonNull(partInfo, "partInfo");
		partInfoMap.put(partType, partInfo);
	}

	/**
	 * Returns the part info for the given type.<br>
	 * If one is not present, returns {@link IPartInfo#empty()}.
	 * @param partType The part type.
	 * @return The part info.
	 */
	public IPartInfo getPartInfo(String partType) {
		return partInfoMap.getOrDefault(partType, empty());
	}

	/**
	 * Whether this {@link PartsData} contains a {@link IPartInfo} for the given type.
	 * @param partType The part type.
	 * @return True if this contains a {@link IPartInfo} for the given type.
	 */
	public boolean hasPartInfo(String partType) {
		return partInfoMap.containsKey(partType) && !partInfoMap.get(partType).isEmpty();
	}

	public List<IPartInfo> getPartInfos() {
		final List<IPartInfo> ret = new ArrayList<>();

		for (Map.Entry<String, IPartInfo> entry : partInfoMap.entrySet())
			if (!entry.getValue().isEmpty())
				ret.add(entry.getValue());

		return ret;
	}

	public Map<String, IPartInfo> getPartInfoMap() {
		return Collections.unmodifiableMap(partInfoMap);
	}

	/**
	 * Clears all textures from each {@link IPartInfo}.
	 */
	public void clearTextures() {
		for (IPartInfo partInfo : partInfoMap.values())
			if (partInfo != null) partInfo.clearGlTexture();
	}

	/**
	 * Returns a copy of this {@link PartsData}.
	 * @return The copy.
	 */
	public PartsData deepCopy() {
		final Map<String, IPartInfo> data = new HashMap<>();
		for (Map.Entry<String, IPartInfo> e : partInfoMap.entrySet())
			data.put(e.getKey(), e.getValue().clone());

		return new PartsData(data);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PartsData partsData)) return false;

		return partInfoMap.equals(partsData.partInfoMap);
	}

	@Override
	public int hashCode() {
		return partInfoMap.hashCode();
	}

	@Override
	public String toString() {
		return "PartsData{" + partInfoMap.entrySet().stream()
				.filter(e -> !e.getValue().isEmpty())
				.map(e -> e.getKey() + "=" + e.getValue().toString())
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
			final JsonObject partInfoMap = new JsonObject();

			for (Map.Entry<String, IPartInfo> entry : src.partInfoMap.entrySet())
				if (!entry.getValue().isEmpty())
					partInfoMap.add(entry.getKey(), context.serialize(entry.getValue()));

			ret.add("partInfoMap", partInfoMap);
			ret.addProperty("version", src.version);

			return ret;
		}

		@Override
		public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject obj = json.getAsJsonObject();
			final int version = obj.has("version") ? obj.get("version").getAsInt() : 0;
			final PartsData ret = new PartsData();

			if (obj.has("partInfoMap")) {
				final JsonObject partInfoMap = obj.get("partInfoMap").getAsJsonObject();

				for (Map.Entry<String, JsonElement> entry : partInfoMap.entrySet()) {
					final String type = version == 0 ? entry.getKey().toLowerCase(Locale.ROOT) : entry.getKey();
					ret.setPartInfo(type, context.deserialize(entry.getValue().getAsJsonObject(), IPartInfo.class));
				}
			} else if (obj.has("partInfos"))
				for (JsonElement elem : obj.get("partInfos").getAsJsonArray()) {
					final JsonObject o = elem.getAsJsonObject();
					final IPartInfo info = context.deserialize(o, IPartInfo.class);
					if (!info.isEmpty()) {
						// Nasty hack to allow <1.10 data to update.
						final String partType = o.has("partType") ? o.get("partType").getAsString().toLowerCase(Locale.ENGLISH) : info.getAttachment();
						ret.setPartInfo(partType, info);
					}
				}

			return ret;
		}
	}
}