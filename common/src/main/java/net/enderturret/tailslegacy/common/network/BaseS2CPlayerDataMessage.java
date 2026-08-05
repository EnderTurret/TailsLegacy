/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.network;

import java.util.UUID;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientInternal;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;
import net.enderturret.tailslegacy.common.part.PartsData;

public interface BaseS2CPlayerDataMessage {

	public static PartsData decodeJson(UUID id, String json) {
		if (TailsInternal.DEBUG_NETWORK)
			TailsPlatform.get().logInfo("[S2CPlayerDataMessage] Received {} = {}", id, json);

		PartsData partsData = PartsData.EMPTY;

		if (json != null && !json.isEmpty())
			try {
				partsData = TailsClientInternal.getClientGson().fromJson(json, PartsData.class);
			} catch (Exception e) {
				TailsPlatform.get().logError("Exception decoding player part data:\n{}", json, e);
			}

		return partsData;
	}

	public static String encodeJson(PartsData partsData) {
		return partsData == null || partsData.isEmpty() ? "" : TailsGsonHelper.SERVER_GSON.toJson(partsData);
	}

	public static void handle(UUID uuid, PartsData partsData) {
		// Don't allow the server to dictate our local part data.
		if (partsData != null && !uuid.equals(TailsClientPlatform.get().getLocalUUID()))
			ClientPlayerPartManager.get().set(uuid, partsData);
	}
}