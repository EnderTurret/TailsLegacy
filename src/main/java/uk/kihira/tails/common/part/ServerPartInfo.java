/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.ColorUtil;
import uk.kihira.tails.common.Tails;

/**
 * The server-side implementation of {@link IPartInfo}.
 * @param partId The part id.
 * @param subTypeId The subtype id.
 * @param textureId The texture id.
 * @param tints The tints.
 * @author EnderTurret
 */
public record ServerPartInfo(ResourceLocation partId, String subTypeId, String textureId, int[] tints) implements IPartInfo {

	@Override
	public IPartInfo clone() {
		return new ServerPartInfo(partId, subTypeId, textureId, tints.clone());
	}

	@Override
	public String getAttachment() {
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

	@Override
	public boolean equals(Object o) {
		return o instanceof ServerPartInfo spi && Objects.equals(partId, spi.partId) && Objects.equals(subTypeId, spi.subTypeId)
				&& Objects.equals(textureId, spi.textureId) && Arrays.equals(tints, spi.tints);
	}

	@Override
	public int hashCode() {
		return Objects.hash(partId, subTypeId, textureId, tints);
	}

	@Override
	public String toString() {
		return "ServerPartInfo[partId=" + partId + ", subTypeId=" + subTypeId + ", textureId=" + textureId + ", tints=" + Arrays.stream(tints).mapToObj(t -> "0x" + ColorUtil.hex(t, true, true)).toList() + "]";
	}

	/**
	 * The serializer for {@link ServerPartInfo}.
	 * @author EnderTurret
	 * @see Tails#SERVER_GSON
	 */
	public static class Serializer implements JsonSerializer<IPartInfo>, JsonDeserializer<IPartInfo> {

		/**
		 * The singleton instance.
		 */
		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {}

		@Override
		public IPartInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			json = json.deepCopy();

			Parts.update(json);

			final JsonObject obj = json.getAsJsonObject();

			final String pId = obj.get("id").getAsString();
			if ("tails:empty".equals(pId)) return IPartInfo.empty();

			final ResourceLocation partId = new ResourceLocation(pId);
			final ResourceLocation newPartId = Parts.remapId(partId);

			if (partId != newPartId && !Parts.TESTING)
				Tails.LOGGER.info("Remapped part id: {} → {}.", partId, newPartId);

			final String subType = obj.get("subType").getAsString();
			final String texture = obj.get("textureId").getAsString();

			final JsonArray tints = obj.get("tints").getAsJsonArray();
			final int[] tintsArr = {tints.get(0).getAsInt(), tints.get(1).getAsInt(), tints.get(2).getAsInt()};

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