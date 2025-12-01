/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;

import net.neoforged.fml.loading.FMLPaths;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.client.part.PartLoadingManager;
import uk.kihira.tails.common2.client.part.PartPath;
import uk.kihira.tails.mixin.client.CubeDefinitionAccess;
import uk.kihira.tails.mixin.client.CubeDeformationAccess;
import uk.kihira.tails.mixin.client.PartDefinitionAccess;

public final class ModelSerializer {

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	private static final Pattern PATTERN = Pattern.compile("\\[\\s*((?:-?[\\d\\.]+,\\n\\s*)*-?[\\d\\.]+)\\n\\s*\\]");
	private static final Pattern NEWLINE = Pattern.compile(",\\n\\s*");

	public static ModelPart bake(PartDefinition root, int textureWidth, int textureHeight, String name) {
		//dump(root, textureWidth, textureHeight, name);

		return root.bake(textureWidth, textureHeight);
	}

	private static void dump(PartDefinition root, int textureWidth, int textureHeight, String name) {
		final Resource base = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, "parts/" + name + ".json"))
				.orElseThrow(() -> new IllegalArgumentException("No such file: parts/" + name + ".json"));

		final JsonObject json;
		try (BufferedReader br = base.openAsReader()) {
			json = JsonParser.parseReader(br).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		json.add("model", serialize(root, textureWidth, textureHeight));

		final Path to = FMLPaths.GAMEDIR.get().resolve("tails_parts").resolve(name + ".json");

		String raw = GSON.toJson(json);
		raw = PATTERN.matcher(raw).replaceAll(mr -> "[" + NEWLINE.matcher(mr.group(1)).replaceAll(", ") + "]");

		try {
			Files.createDirectories(to.getParent());
			Files.writeString(to, raw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JsonElement serialize(PartDefinition root, int textureWidth, int textureHeight) {
		final JsonObject obj = new JsonObject();

		obj.add("texture_size", simpleNumberArray(textureWidth, textureHeight));

		serialize(root, true, obj);

		return obj;
	}

	private static JsonObject serialize(PartDefinition def, boolean root, @Nullable JsonObject obj) {
		if (obj == null) obj = new JsonObject();
		final PartDefinitionAccess access = (PartDefinitionAccess) def;

		if (!root) {
			final PartPose pose = access.tails$partPose();
			if (!isZero(pose))
				obj.add("pose", serialize(pose));

			if (!access.tails$cubes().isEmpty()) {
				final JsonArray cubes = new JsonArray();

				for (CubeDefinition cube : access.tails$cubes())
					cubes.add(serialize(cube));

				obj.add("cubes", cubes);
			}
		}

		if (!access.tails$children().isEmpty()) {
			final JsonObject children;
			if (!root) {
				children = new JsonObject();
				obj.add("children", children);
			} else
				children = obj;

			for (Map.Entry<String, PartDefinition> entry : access.tails$children().entrySet())
				children.add(entry.getKey(), serialize(entry.getValue(), false, null));
		}

		return obj;
	}

	private static JsonObject serialize(PartPose pose) {
		final JsonObject poseO = new JsonObject();

		if (pose.x != 0 || pose.y != 0 || pose.z != 0)
			poseO.add("pos", simpleNumberArray(pose.x, pose.y, pose.z));

		if (pose.xRot != 0 || pose.yRot != 0 || pose.zRot != 0)
			poseO.add("rotation", simpleNumberArray(pose.xRot, pose.yRot, pose.zRot));

		return poseO;
	}

	static boolean isZero(PartPose pose) {
		return pose.x == 0 && pose.y == 0 && pose.z == 0 && pose.xRot == 0 && pose.yRot == 0 && pose.zRot == 0;
	}

	private static JsonObject serialize(CubeDefinition cube) {
		final CubeDefinitionAccess access = (CubeDefinitionAccess) (Object) cube;
		final JsonObject cubeO = new JsonObject();

		if (access.tails$comment() != null)
			cubeO.addProperty("comment", access.tails$comment());

		cubeO.add("uv", simpleNumberArray(access.tails$texCoord().u(), access.tails$texCoord().v()));

		cubeO.add("pos", simpleNumberArray(
				access.tails$origin().x,
				access.tails$origin().y,
				access.tails$origin().z));

		cubeO.add("size", simpleNumberArray(
				access.tails$dimensions().x,
				access.tails$dimensions().y,
				access.tails$dimensions().z));

		final CubeDeformationAccess grow = (CubeDeformationAccess) access.tails$grow();
		if (grow.tails$growX() != 0 || grow.tails$growY() != 0 || grow.tails$growZ() != 0)
			if (grow.tails$growX() == grow.tails$growY() && grow.tails$growX() == grow.tails$growZ())
				cubeO.addProperty("grow", grow.tails$growX());
			else
				cubeO.add("grow", simpleNumberArray(
						grow.tails$growX(),
						grow.tails$growY(),
						grow.tails$growZ()));

		if (access.tails$mirror())
			cubeO.addProperty("mirror", true);

		return cubeO;
	}

	private static JsonArray simpleNumberArray(Number... numbers) {
		final JsonArray ret = new JsonArray(numbers.length);

		for (Number num : numbers)
			if (num.doubleValue() == num.intValue())
				ret.add(new JsonPrimitive(num.intValue()));
			else
				ret.add(new JsonPrimitive(num.floatValue()));

		return ret;
	}

	public static RootPartDefinition deserializeRoot(JsonObject obj) {
		final PartDefinition def = deserialize(obj, true);
		final JsonArray textureSize = GsonHelper.getAsJsonArray(obj, "texture_size");
		final List<PartPath> hidden = obj.has("hidden_parts") ? PartLoadingManager.getAsStringArray(obj, "hidden_parts").stream().map(PartPath::new).toList() : List.of();

		return new RootPartDefinition(def,
				GsonHelper.convertToInt(textureSize.get(0), "texture_size[0]"),
				GsonHelper.convertToInt(textureSize.get(1), "texture_size[1]"),
				hidden);
	}

	public static PartDefinition deserialize(JsonObject obj, boolean root) {
		final PartPose pose = obj.has("pose") ? deserializePose(GsonHelper.getAsJsonObject(obj, "pose")) : PartPose.ZERO;
		List<CubeDefinition> cubes;

		if (obj.has("cubes")) {
			final JsonArray array = GsonHelper.getAsJsonArray(obj, "cubes");
			cubes = new ArrayList<>(array.size());

			for (int i = 0; i < array.size(); i++)
				cubes.add(deserializeCube(GsonHelper.convertToJsonObject(array.get(i), "cubes[" + i + "]")));

			cubes = ImmutableList.copyOf(cubes);
		} else
			cubes = ImmutableList.of();

		final PartDefinition ret = PartDefinitionAccess.tails$new(cubes, pose);

		final JsonElement childrenE = root ? obj : obj.get("children");

		if (childrenE != null) {
			final PartDefinitionAccess access = (PartDefinitionAccess) ret;
			final JsonObject children = GsonHelper.convertToJsonObject(childrenE, "children");
			for (Map.Entry<String, JsonElement> entry : children.entrySet()) {
				if (root && ("texture_size".equals(entry.getKey()) || "hidden_parts".equals(entry.getKey()))) continue;
				final PartDefinition child = deserialize(GsonHelper.convertToJsonObject(entry.getValue(), "children." + entry.getKey()), false);
				access.tails$children().put(entry.getKey(), child);
			}
		}

		return ret;
	}

	private static PartPose deserializePose(JsonObject obj) {
		final float x, y, z, xRot, yRot, zRot;

		if (obj.has("pos")) {
			final JsonArray a = GsonHelper.getAsJsonArray(obj, "pos");
			x = GsonHelper.convertToFloat(a.get(0), "pos[0]");
			y = GsonHelper.convertToFloat(a.get(1), "pos[1]");
			z = GsonHelper.convertToFloat(a.get(2), "pos[2]");
		} else x = y = z = 0;

		if (obj.has("rotation")) {
			final JsonArray a = GsonHelper.getAsJsonArray(obj, "rotation");
			xRot = GsonHelper.convertToFloat(a.get(0), "rotation[0]");
			yRot = GsonHelper.convertToFloat(a.get(1), "rotation[1]");
			zRot = GsonHelper.convertToFloat(a.get(2), "rotation[2]");
		} else xRot = yRot = zRot = 0;

		return PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot);
	}

	private static final Set<Direction> ALL_VISIBLE = EnumSet.allOf(Direction.class);

	private static CubeDefinition deserializeCube(JsonObject obj) {
		final String comment = obj.has("comment") ? GsonHelper.getAsString(obj, "comment") : null;

		final JsonArray uvA = GsonHelper.getAsJsonArray(obj, "uv");
		final float u = GsonHelper.convertToFloat(uvA.get(0), "uv[0]"),
				v = GsonHelper.convertToFloat(uvA.get(1), "uv[1]");

		final JsonArray posA = GsonHelper.getAsJsonArray(obj, "pos");
		final float x = GsonHelper.convertToFloat(posA.get(0), "pos[0]"),
				y = GsonHelper.convertToFloat(posA.get(1), "pos[1]"),
				z = GsonHelper.convertToFloat(posA.get(2), "pos[2]");

		final JsonArray sizeA = GsonHelper.getAsJsonArray(obj, "size");
		final float sizeX = GsonHelper.convertToFloat(sizeA.get(0), "size[0]"),
				sizeY = GsonHelper.convertToFloat(sizeA.get(1), "size[1]"),
				sizeZ = GsonHelper.convertToFloat(sizeA.get(2), "size[2]");

		final CubeDeformation grow;
		if (obj.has("grow")) {
			final JsonElement growE = obj.get("grow");
			if (growE.isJsonPrimitive() && growE.getAsJsonPrimitive().isNumber())
				grow = new CubeDeformation(growE.getAsFloat());
			else {
				final JsonArray a = GsonHelper.convertToJsonArray(growE, "grow");
				grow = new CubeDeformation(
						GsonHelper.convertToFloat(a.get(0), "grow[0]"),
						GsonHelper.convertToFloat(a.get(1), "grow[1]"),
						GsonHelper.convertToFloat(a.get(2), "grow[2]")
						);
			}
		} else grow = CubeDeformation.NONE;

		final boolean mirror = GsonHelper.getAsBoolean(obj, "mirror", false);

		final Set<Direction> visible;
		if (obj.has("visible")) {
			visible = EnumSet.noneOf(Direction.class);
			final JsonArray array = GsonHelper.getAsJsonArray(obj, "visible");
			for (int i = 0; i < array.size(); i++)
				visible.add(Direction.byName(GsonHelper.convertToString(array.get(i), "visible[" + i + "]")));
		} else visible = ALL_VISIBLE;

		return CubeDefinitionAccess.tails$new(comment,
				u, v, x, y, z, sizeX, sizeY, sizeZ,
				grow, mirror, 1F, 1F, visible);
	}

	public static record RootPartDefinition(PartDefinition definition, int textureWidth, int textureHeight, List<PartPath> hiddenParts) {

		public ModelPart bake() {
			final ModelPart ret = definition.bake(textureWidth, textureHeight);
			for (PartPath path : hiddenParts) path.traverse(ret).visible = false;
			return ret;
		}
	}
}