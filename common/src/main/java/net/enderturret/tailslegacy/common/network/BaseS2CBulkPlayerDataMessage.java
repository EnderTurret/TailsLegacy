/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.network;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientInternal;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;
import net.enderturret.tailslegacy.common.part.PartsData;

public interface BaseS2CBulkPlayerDataMessage {

	public static final TypeToken<Map<UUID, PartsData>> PART_DATA_MAP_TYPE = new TypeToken<Map<UUID, PartsData>>() {};

	public static Map<UUID, PartsData> decodeJson(String json) {
		if (TailsInternal.DEBUG_NETWORK)
			TailsPlatform.get().logInfo("[S2CBulkPlayerDataMessage] Received {}", json);

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

		final UUID ourId = TailsClientPlatform.get().getLocalUUID();
		for (Map.Entry<UUID, PartsData> entry : map.entrySet())
			if (!entry.getKey().equals(ourId))
				ClientPlayerPartManager.get().set(entry.getKey(), entry.getValue());
	}
}