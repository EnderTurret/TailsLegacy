package uk.kihira.tails.common2.network;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import uk.kihira.tails.common2.TailsInternal;
import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.gson.TailsGsonHelper;
import uk.kihira.tails.common2.part.PartsData;

public interface BasePlayerDataMapMessage {

	public static final TypeToken<Map<UUID, PartsData>> PART_DATA_MAP_TYPE = new TypeToken<Map<UUID, PartsData>>() {};

	public static Map<UUID, PartsData> decodeJson(String json) {
		if (TailsInternal.DEBUG_NETWORK)
			TailsPlatform.get().logInfo("[PlayerDataMapMessage] Received {}", json);

		Map<UUID, PartsData> partsDataMap = Collections.emptyMap();

		try {
			partsDataMap = TailsPlatform.get().getSidedGson().fromJson(json, PART_DATA_MAP_TYPE.getType());
		} catch (Exception e) {
			TailsPlatform.get().logError("Exception decoding player part data:\n{}", json, e);
		}

		return partsDataMap;
	}

	public static String encodeJson(Map<UUID, PartsData> map) {
		return TailsGsonHelper.SERVER_GSON.toJson(map);
	}

	public static void handle(Map<UUID, PartsData> map) {
		if (map == null) return;

		for (Map.Entry<UUID, PartsData> entry : map.entrySet())
			TailsPlatform.get().getPartManager().set(entry.getKey(), entry.getValue());
	}
}