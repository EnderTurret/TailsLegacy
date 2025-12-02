package uk.kihira.tails.common.network;

import uk.kihira.tails.common.TailsInternal;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.gson.TailsGsonHelper;
import uk.kihira.tails.common.part.PartsData;

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
		return partsData == null || partsData.isEmpty() ? "" : TailsPlatform.get().getSidedGson().toJson(partsData);
	}
}