/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import uk.kihira.tails.common.TailsInternal;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.TailsClientInternal;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.gson.TailsGsonHelper;
import uk.kihira.tails.common.part.PartsData;

public interface BasePlayerDataMapMessage {

	public static final TypeToken<Map<UUID, PartsData>> PART_DATA_MAP_TYPE = new TypeToken<Map<UUID, PartsData>>() {};

	public static Map<UUID, PartsData> decodeJson(String json) {
		if (TailsInternal.DEBUG_NETWORK)
			TailsPlatform.get().logInfo("[PlayerDataMapMessage] Received {}", json);

		Map<UUID, PartsData> partsDataMap = Collections.emptyMap();

		try {
			partsDataMap = TailsClientInternal.getClientGson().fromJson(json, PART_DATA_MAP_TYPE.getType());
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
			ClientPlayerPartManager.get().set(entry.getKey(), entry.getValue());
	}
}