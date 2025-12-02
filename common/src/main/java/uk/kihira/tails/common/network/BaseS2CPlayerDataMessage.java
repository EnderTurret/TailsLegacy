package uk.kihira.tails.common.network;

import java.util.UUID;

import uk.kihira.tails.common.TailsInternal;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.gson.TailsGsonHelper;
import uk.kihira.tails.common.part.PartsData;

public interface BaseS2CPlayerDataMessage {

	public static PartsData decodeJson(UUID id, String json) {
		if (TailsInternal.DEBUG_NETWORK)
			TailsPlatform.get().logInfo("[S2CPlayerDataMessage] Received {} = {}", id, json);

		PartsData partsData = PartsData.EMPTY;

		if (json != null && !json.isEmpty())
			try {
				partsData = TailsPlatform.get().getSidedGson().fromJson(json, PartsData.class);
			} catch (Exception e) {
				TailsPlatform.get().logError("Exception decoding player part data:\n{}", json, e);
			}

		return partsData;
	}

	public static String encodeJson(PartsData partsData) {
		return partsData == null || partsData.isEmpty() ? "" : TailsGsonHelper.SERVER_GSON.toJson(partsData);
	}

	public static void handle(UUID uuid, PartsData partsData) {
		if (partsData != null)
			TailsPlatform.get().getPartManager().set(uuid, partsData);
	}
}