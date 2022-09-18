/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * The part data class.<br>
 * Holds {@link IPartInfo} for every {@link PartType}.
 */
public class PartsData {

	public static final PartsData EMPTY = new PartsData() {
		@Override
		public IPartInfo getPartInfo(PartType partType) { return IPartInfo.empty(); }
		@Override
		public void setPartInfo(PartType partType, IPartInfo partInfo) {}
		@Override
		public void clearTextures() {}
		@Override
		public boolean hasPartInfo(PartType partType) { return false; }
		@Override
		public String toString() { return "PartsData#EMPTY"; }
		@Override
		public boolean isEmpty() { return true; }
	};

	private final Map<PartType, IPartInfo> partInfoMap = new EnumMap<>(PartType.class);

	/**
	 * The version.<br>
	 * 1 is the current version.
	 */
	private final int version = 1;

	public PartsData() {
		for (PartType type : PartType.values())
			partInfoMap.put(type, IPartInfo.empty());
	}

	public PartsData(Map<PartType, IPartInfo> partData) {
		this();
		partInfoMap.putAll(partData);
	}

	public boolean isEmpty() {
		return false;
	}

	/**
	 * Sets the {@link IPartInfo} for the given type as the given part info.
	 * @param partType The part type to set the part info as.
	 * @param partInfo The part info.
	 */
	public void setPartInfo(PartType partType, IPartInfo partInfo) {
		partInfoMap.put(partType, partInfo);
	}

	/**
	 * Returns the part info for the given type.<br>
	 * If one is not present, returns {@link IPartInfo#empty()}.
	 * @param partType The part type.
	 * @return The part info.
	 */
	public IPartInfo getPartInfo(PartType partType) {
		return partInfoMap.getOrDefault(partType, IPartInfo.empty());
	}

	/**
	 * Whether this {@link PartsData} contains a {@link IPartInfo} for the given type.
	 * @param partType The part type.
	 * @return True if this contains a {@link IPartInfo} for the given type.
	 */
	public boolean hasPartInfo(PartType partType) {
		return partInfoMap.containsKey(partType) && !partInfoMap.get(partType).isEmpty();
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
		final Map<PartType, IPartInfo> data = new EnumMap<>(PartType.class);
		for (Map.Entry<PartType, IPartInfo> e : partInfoMap.entrySet())
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
				.map(e -> e.getKey().getId() + "=" + e.getValue().toString())
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

			for (Map.Entry<PartType, IPartInfo> entry : src.partInfoMap.entrySet())
				if (!entry.getValue().isEmpty())
					partInfoMap.add(entry.getKey().getId(), context.serialize(entry.getValue()));

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
					final PartType type = PartType.forId(version == 0 ? entry.getKey().toLowerCase(Locale.ROOT) : entry.getKey());
					ret.setPartInfo(type, context.deserialize(entry.getValue().getAsJsonObject(), IPartInfo.class));
				}
			} else if (obj.has("partInfos"))
				for (JsonElement elem : obj.get("partInfos").getAsJsonArray()) {
					final IPartInfo info = context.deserialize(elem.getAsJsonObject(), IPartInfo.class);
					if (!info.isEmpty())
						ret.setPartInfo(info.getType(), info);
				}

			return ret;
		}
	}
}