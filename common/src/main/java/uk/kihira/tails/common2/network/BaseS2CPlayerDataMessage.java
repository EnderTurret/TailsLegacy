package uk.kihira.tails.common2.network;

import java.util.UUID;

import uk.kihira.tails.common2.TailsInternal;
import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.gson.TailsGsonHelper;
import uk.kihira.tails.common2.part.PartsData;

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