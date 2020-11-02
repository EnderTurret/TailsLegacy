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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

public class PartsData {

	private final Map<PartType, PartInfo> partInfoMap = new EnumMap<>(PartType.class);

	private final int version = 1;

	public PartsData() {}
	public PartsData(Map<PartType,PartInfo> partData) {
		partInfoMap.putAll(partData);
	}

	public void setPartInfo(PartType partType, PartInfo partInfo) {
		partInfoMap.put(partType, partInfo);
	}

	public PartInfo getPartInfo(PartType partType) {
		return partInfoMap.getOrDefault(partType, PartInfo.none(partType));
	}

	public boolean hasPartInfo(PartType partType) {
		return partInfoMap.containsKey(partType) && !partInfoMap.get(partType).isEmpty();
	}

	public void clearTextures() {
		for (PartInfo partInfo : partInfoMap.values())
			if (partInfo != null) partInfo.setTexture(null);
	}

	public PartsData deepCopy() {
		final Map<PartType,PartInfo> data = new EnumMap<>(PartType.class);
		for (Map.Entry<PartType,PartInfo> e : partInfoMap.entrySet()) {
			data.put(e.getKey(), e.getValue().deepCopy());
		}

		return new PartsData(data);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof PartsData)) return false;

		final PartsData partsData = (PartsData) o;

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

	public static class Serializer implements JsonDeserializer<PartsData>, JsonSerializer<PartsData> {

		@Override
		public JsonElement serialize(PartsData src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject ret = new JsonObject();
			final JsonObject partInfoMap = new JsonObject();

			for (Map.Entry<PartType,PartInfo> entry : src.partInfoMap.entrySet()) {
				if (!entry.getValue().isEmpty())
					partInfoMap.add(entry.getKey().getId(), context.serialize(entry.getValue()));
			}

			ret.add("partInfoMap", partInfoMap);

			return ret;
		}

		@Override
		public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject obj = json.getAsJsonObject();
			final int version = obj.has("version") ? obj.get("version").getAsInt() : 0;
			final PartsData ret = new PartsData();

			if (obj.has("partInfoMap")) {
				final JsonObject partInfoMap = obj.get("partInfoMap").getAsJsonObject();

				for (Map.Entry<String,JsonElement> entry : partInfoMap.entrySet()) {
					final PartType type = PartType.forId(version == 0 ? entry.getKey().toLowerCase(Locale.ROOT) : entry.getKey());
					ret.setPartInfo(type, PartInfo.deserialize(entry.getValue().getAsJsonObject()));
				}
			} else if (obj.has("partInfos")) {
				for (JsonElement elem : obj.get("partInfos").getAsJsonArray()) {
					final PartInfo info = PartInfo.deserialize(elem.getAsJsonObject());
					if (!info.isEmpty())
						ret.setPartInfo(info.getPartType(), info);
				}
			}

			return ret;
		}
	}
}