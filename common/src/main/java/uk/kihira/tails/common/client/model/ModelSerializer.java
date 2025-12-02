/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import uk.kihira.tails.common.TailsDirection;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.part.PartLoadingManager;
import uk.kihira.tails.common.client.part.PartPath;
import uk.kihira.tails.common.gson.TailsGsonHelper;

public final class ModelSerializer {

	public static RootPartDefinition deserializeRoot(JsonObject obj) {
		final TailsPartDefinition def = deserialize(obj, true);
		final JsonArray textureSize = TailsGsonHelper.getAsJsonArray(obj, "texture_size");
		final List<PartPath> hidden = obj.has("hidden_parts") ? PartLoadingManager.getAsStringArray(obj, "hidden_parts").stream().map(PartPath::new).collect(Collectors.toList()) : Collections.emptyList();

		return new RootPartDefinition(def,
				TailsGsonHelper.convertToInt(textureSize.get(0), "texture_size[0]"),
				TailsGsonHelper.convertToInt(textureSize.get(1), "texture_size[1]"),
				hidden);
	}

	public static TailsPartDefinition deserialize(JsonObject obj, boolean root) {
		List<TailsCubeDefinition> cubes;

		if (obj.has("cubes")) {
			final JsonArray array = TailsGsonHelper.getAsJsonArray(obj, "cubes");
			cubes = new ArrayList<>(array.size());

			for (int i = 0; i < array.size(); i++)
				cubes.add(deserializeCube(TailsGsonHelper.convertToJsonObject(array.get(i), "cubes[" + i + "]")));

			cubes = Collections.unmodifiableList(new ArrayList<>(cubes));
		} else
			cubes = Collections.emptyList();

		final TailsPartDefinition ret = new TailsPartDefinition(cubes);

		if (obj.has("pose"))
			deserializePose(TailsGsonHelper.getAsJsonObject(obj, "pose"), ret);

		final JsonElement childrenE = root ? obj : obj.get("children");

		if (childrenE != null) {
			final JsonObject children = TailsGsonHelper.convertToJsonObject(childrenE, "children");
			for (Map.Entry<String, JsonElement> entry : children.entrySet()) {
				if (root && ("texture_size".equals(entry.getKey()) || "hidden_parts".equals(entry.getKey()))) continue;
				final TailsPartDefinition child = deserialize(TailsGsonHelper.convertToJsonObject(entry.getValue(), "children." + entry.getKey()), false);
				ret.children.put(entry.getKey(), child);
			}
		}

		return ret;
	}

	private static void deserializePose(JsonObject obj, TailsPartDefinition part) {
		if (obj.has("pos")) {
			final JsonArray a = TailsGsonHelper.getAsJsonArray(obj, "pos");
			part.xOffset = TailsGsonHelper.convertToFloat(a.get(0), "pos[0]");
			part.yOffset = TailsGsonHelper.convertToFloat(a.get(1), "pos[1]");
			part.zOffset = TailsGsonHelper.convertToFloat(a.get(2), "pos[2]");
		}

		if (obj.has("rotation")) {
			final JsonArray a = TailsGsonHelper.getAsJsonArray(obj, "rotation");
			part.xRot = TailsGsonHelper.convertToFloat(a.get(0), "rotation[0]");
			part.yRot = TailsGsonHelper.convertToFloat(a.get(1), "rotation[1]");
			part.zRot = TailsGsonHelper.convertToFloat(a.get(2), "rotation[2]");
		}
	}

	private static final Set<TailsDirection> ALL_VISIBLE = EnumSet.allOf(TailsDirection.class);

	private static TailsCubeDefinition deserializeCube(JsonObject obj) {
		final JsonArray uvA = TailsGsonHelper.getAsJsonArray(obj, "uv");
		final float u = TailsGsonHelper.convertToFloat(uvA.get(0), "uv[0]"),
				v = TailsGsonHelper.convertToFloat(uvA.get(1), "uv[1]");

		final JsonArray posA = TailsGsonHelper.getAsJsonArray(obj, "pos");
		final float x = TailsGsonHelper.convertToFloat(posA.get(0), "pos[0]"),
				y = TailsGsonHelper.convertToFloat(posA.get(1), "pos[1]"),
				z = TailsGsonHelper.convertToFloat(posA.get(2), "pos[2]");

		final JsonArray sizeA = TailsGsonHelper.getAsJsonArray(obj, "size");
		final float sizeX = TailsGsonHelper.convertToFloat(sizeA.get(0), "size[0]"),
				sizeY = TailsGsonHelper.convertToFloat(sizeA.get(1), "size[1]"),
				sizeZ = TailsGsonHelper.convertToFloat(sizeA.get(2), "size[2]");

		final float growX, growY, growZ;
		if (obj.has("grow")) {
			final JsonElement growE = obj.get("grow");
			if (growE.isJsonPrimitive() && growE.getAsJsonPrimitive().isNumber())
				growX = growY = growZ = growE.getAsFloat();
			else {
				final JsonArray a = TailsGsonHelper.convertToJsonArray(growE, "grow");
				growX = TailsGsonHelper.convertToFloat(a.get(0), "grow[0]");
				growY = TailsGsonHelper.convertToFloat(a.get(1), "grow[1]");
				growZ = TailsGsonHelper.convertToFloat(a.get(2), "grow[2]");
			}
		} else growX = growY = growZ = 0;

		final boolean mirror = TailsGsonHelper.getAsBoolean(obj, "mirror", false);

		final Set<TailsDirection> visible;
		if (obj.has("visible")) {
			visible = EnumSet.noneOf(TailsDirection.class);
			final JsonArray array = TailsGsonHelper.getAsJsonArray(obj, "visible");
			for (int i = 0; i < array.size(); i++)
				visible.add(TailsDirection.byName(TailsGsonHelper.convertToString(array.get(i), "visible[" + i + "]")));
		} else visible = ALL_VISIBLE;

		return new TailsCubeDefinition(
				x, y, z, sizeX, sizeY, sizeZ,
				growX, growY, growZ, mirror, u, v, visible);
	}

	public static final class RootPartDefinition {

		public final TailsPartDefinition definition;
		public final int textureWidth;
		public final int textureHeight;
		public final List<PartPath> hiddenParts;

		public RootPartDefinition(TailsPartDefinition definition, int textureWidth, int textureHeight, List<PartPath> hiddenParts) {
			this.definition = definition;
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.hiddenParts = hiddenParts;
		}

		public TailsModelPart bake() {
			final TailsModelPart ret = TailsClientPlatform.get().bake(definition, textureWidth, textureHeight);
			for (PartPath path : hiddenParts) path.traverse(ret).t$setVisible(false);
			return ret;
		}
	}
}