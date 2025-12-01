package uk.kihira.tails.common_gson.client;

import java.lang.reflect.Type;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.part.PartsData;
import uk.kihira.tails.common_gson.PartsDataSerializer;

@Internal
public class ClientPartsDataSerializer extends PartsDataSerializer {

	@Override
	public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return ClientPartsData.clone(super.deserialize(json, typeOfT, context));
	}
}