package uk.kihira.tails.common.part;

import java.lang.reflect.Type;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;

public record ServerPartInfo(ResourceLocation partId, String subTypeId, String textureId, int[] tints) implements IPartInfo {

	@Override
	public IPartInfo clone() {
		return new ServerPartInfo(partId, subTypeId, textureId, tints.clone());
	}

	@Override
	public PartType getType() {
		return null;
	}

	@Override
	public ResourceLocation getPartId() {
		return partId;
	}

	@Override
	public String getSubTypeId() {
		return subTypeId;
	}

	@Override
	public String getTextureId() {
		return textureId;
	}

	@Override
	public int[] getTints() {
		return tints;
	}

	public static class Serializer implements JsonSerializer<IPartInfo>, JsonDeserializer<IPartInfo> {

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {}

		@Override
		public IPartInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			Parts.update(json);

			final JsonObject obj = json.getAsJsonObject();

			final String pId = obj.get("id").getAsString();
			if ("tails:empty".equals(pId)) return IPartInfo.empty();

			final ResourceLocation partId = new ResourceLocation(pId);
			final ResourceLocation newPartId = Parts.remapId(partId);

			if (partId != newPartId)
				Tails.LOGGER.info("Remapped part id: {} → {}.", partId, newPartId);

			final String subType = obj.get("subType").getAsString();
			final String texture = obj.get("textureId").getAsString();

			final JsonArray tints = obj.get("tints").getAsJsonArray();
			final int[] tintsArr = new int[] {tints.get(0).getAsInt(), tints.get(1).getAsInt(), tints.get(2).getAsInt()};

			return new ServerPartInfo(newPartId, subType, texture, tintsArr);
		}

		@Override
		public JsonElement serialize(IPartInfo src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject obj = new JsonObject();

			obj.addProperty("id", src.getPartId().toString());

			if (!src.isEmpty()) {
				obj.addProperty("subType", src.getSubTypeId());
				obj.addProperty("textureId", src.getTextureId());

				final JsonArray tints = new JsonArray();
				tints.add(src.getTints()[0]);
				tints.add(src.getTints()[1]);
				tints.add(src.getTints()[2]);
				obj.add("tints", tints);
			}

			return obj;
		}
	}
}