/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.network;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientInternal;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;
import net.enderturret.tailslegacy.common.part.PartsData;

public interface BaseC2SPlayerDataMessage {

	public static PartsData decodeJson(String json) {
		if (TailsInternal.DEBUG_NETWORK)
			TailsPlatform.get().logInfo("[C2SPlayerDataMessage] Received {}", json);

		PartsData partsData = PartsData.EMPTY;

		if (json != null && !json.isEmpty())
			try {
				partsData = TailsGsonHelper.SERVER_GSON.fromJson(json, PartsData.class);
			} catch (Exception e) {
				TailsPlatform.get().logError("Exception decoding player part data:\n{}", json, e);
			}

		return partsData;
	}

	public static String encodeJson(PartsData partsData) {
		return partsData == null || partsData.isEmpty() ? "" : TailsClientInternal.getClientGson().toJson(partsData);
	}
}