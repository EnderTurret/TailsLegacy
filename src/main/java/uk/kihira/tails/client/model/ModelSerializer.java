/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

import uk.kihira.tails.common2.client.TailsClientPlatform;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.TailsCubeDefinition;
import uk.kihira.tails.common2.client.model.TailsPartDefinition;
import uk.kihira.tails.common2.client.part.PartLoadingManager;
import uk.kihira.tails.common2.client.part.PartPath;
import uk.kihira.tails.common_gson.TailsGsonHelper;

public final class ModelSerializer {

	public static boolean isZero(PartPose pose) {
		return pose.x == 0 && pose.y == 0 && pose.z == 0 && pose.xRot == 0 && pose.yRot == 0 && pose.zRot == 0;
	}

	public static RootPartDefinition deserializeRoot(JsonObject obj) {
		final TailsPartDefinition def = deserialize(obj, true);
		final JsonArray textureSize = TailsGsonHelper.getAsJsonArray(obj, "texture_size");
		final List<PartPath> hidden = obj.has("hidden_parts") ? PartLoadingManager.getAsStringArray(obj, "hidden_parts").stream().map(PartPath::new).toList() : List.of();

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

			cubes = ImmutableList.copyOf(cubes);
		} else
			cubes = ImmutableList.of();

		final TailsPartDefinition ret = new TailsPartDefinition(cubes);

		if (obj.has("pose")) {
			deserializePose(GsonHelper.getAsJsonObject(obj, "pose"), ret);
		}

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

	private static final Set<Direction> ALL_VISIBLE = EnumSet.allOf(Direction.class);

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
				final JsonArray a = GsonHelper.convertToJsonArray(growE, "grow");
				growX = TailsGsonHelper.convertToFloat(a.get(0), "grow[0]");
				growY = TailsGsonHelper.convertToFloat(a.get(1), "grow[1]");
				growZ = TailsGsonHelper.convertToFloat(a.get(2), "grow[2]");
			}
		} else growX = growY = growZ = 0;

		final boolean mirror = TailsGsonHelper.getAsBoolean(obj, "mirror", false);

		final Set<Direction> visible;
		if (obj.has("visible")) {
			visible = EnumSet.noneOf(Direction.class);
			final JsonArray array = TailsGsonHelper.getAsJsonArray(obj, "visible");
			for (int i = 0; i < array.size(); i++)
				visible.add(Direction.byName(TailsGsonHelper.convertToString(array.get(i), "visible[" + i + "]")));
		} else visible = ALL_VISIBLE;

		return new TailsCubeDefinition(
				x, y, z, sizeX, sizeY, sizeZ,
				growX, growY, growZ, mirror, u, v, visible);
	}

	public static record RootPartDefinition(TailsPartDefinition definition, int textureWidth, int textureHeight, List<PartPath> hiddenParts) {

		public TailsModelPart bake() {
			final TailsModelPart ret = TailsClientPlatform.get().bake(definition, textureWidth, textureHeight);
			for (PartPath path : hiddenParts) path.traverse(ret).t$setVisible(false);
			return ret;
		}
	}
}