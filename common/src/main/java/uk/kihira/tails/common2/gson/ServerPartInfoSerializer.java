package uk.kihira.tails.common2.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.part.IPartInfo;
import uk.kihira.tails.common2.part.ServerPartInfo;

/**
 * The serializer for {@link ServerPartInfo}.
 * @author EnderTurret
 */
public class ServerPartInfoSerializer implements JsonSerializer<IPartInfo>, JsonDeserializer<IPartInfo> {

	/**
	 * The singleton instance.
	 */
	public static final ServerPartInfoSerializer INSTANCE = new ServerPartInfoSerializer();

	private ServerPartInfoSerializer() {}

	@Override
	public IPartInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		json = GsonParts.update(json);

		final JsonObject obj = json.getAsJsonObject();

		final String pId = TailsGsonHelper.getAsString(obj, "id");
		if ("tails:empty".equals(pId)) return IPartInfo.empty();

		final TResourceLocation partId = TailsPlatform.get().parseResourceLocation(pId);
		final String subType = TailsGsonHelper.getAsString(obj, "subType");
		final String texture = TailsGsonHelper.getAsString(obj, "textureId");

		final JsonArray tints = TailsGsonHelper.getAsJsonArray(obj, "tints");
		final int[] tintsArr = {
				tints.get(0).getAsInt() | 0xFF000000,
				tints.get(1).getAsInt() | 0xFF000000,
				tints.get(2).getAsInt() | 0xFF000000
		};

		return new ServerPartInfo(partId, subType, texture, tintsArr);
	}

	@Override
	public JsonElement serialize(IPartInfo src, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject obj = new JsonObject();

		obj.addProperty("id", src.getPartId().toString());

		if (!src.isEmpty()) {
			obj.addProperty("subType", src.getSubTypeId());
			obj.addProperty("textureId", src.getTextureId());

			final JsonArray tints = new JsonArray();
			tints.add(new JsonPrimitive(src.getTints()[0] & 0xFFFFFF));
			tints.add(new JsonPrimitive(src.getTints()[1] & 0xFFFFFF));
			tints.add(new JsonPrimitive(src.getTints()[2] & 0xFFFFFF));
			obj.add("tints", tints);
		}

		return obj;
	}
}