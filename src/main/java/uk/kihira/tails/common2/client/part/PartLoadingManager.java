/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.part;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import uk.kihira.tails.client.model.ModelSerializer;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common_gson.ResourceManagerWrapper;
import uk.kihira.tails.common_gson.TailsGsonHelper;

/**
 * Manages loading all of the parts, subtypes, and part textures.
 * This is easily one of the most complicated parts of the mod.
 * @author EnderTurret
 */
@Internal
public class PartLoadingManager {

	private final Runnable clear;
	private final BiConsumer<List<Part>, Map<AttachmentPoint, List<TResourceLocation>>> onComplete;

	/**
	 * Whether to dump registry contents on (re)load.
	 */
	private static final boolean DEBUG_REGISTRIES = Boolean.getBoolean("tails.debugRegistries");

	/**
	 * @param clear A callback to run when the manager is cleared.
	 * @param onComplete A callback to run when the manager finished loading part data.
	 */
	public PartLoadingManager(Runnable clear, BiConsumer<List<Part>, Map<AttachmentPoint, List<TResourceLocation>>> onComplete) {
		this.clear = clear;
		this.onComplete = onComplete;
	}

	public void reload(ResourceManagerWrapper manager) {
		clear.run();

		final List<Part> parts = new ArrayList<>();

		try {
			parts.addAll(reloadParts(manager));
		} catch (Exception e) {
			Tails.LOGGER.fatal("Critical part loading failure!", e);
		}

		final Map<AttachmentPoint, List<TResourceLocation>> ordering = new LinkedHashMap<>();

		try {
			ordering.putAll(readOrdering(manager));
		} catch (Exception e) {
			Tails.LOGGER.error("Failed to apply part ordering!", e);
		}

		onComplete.accept(parts, ordering);
	}

	private static final Type ORDERING_TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();

	/**
	 * Reads the root part ordering.
	 * @param manager The resource manager.
	 * @return The root part ordering.
	 */
	private Map<AttachmentPoint, List<TResourceLocation>> readOrdering(ResourceManagerWrapper manager) {
		final Map<AttachmentPoint, List<TResourceLocation>> ordering = new LinkedHashMap<>();

		final TResourceLocation loc = TailsPlatform.get().newResourceLocation("part_ordering.json");
		final JsonElement json = manager.getJson(loc);
		if (json == null) return Map.of();

		final Map<String, List<String>> rawOrdering = LocalPartManager.GSON.fromJson(json, ORDERING_TYPE);

		for (Map.Entry<String, List<String>> entry : rawOrdering.entrySet()) {
			final AttachmentPoint attachment = AttachmentPoints.get(entry.getKey());
			if (attachment == null)
				Tails.LOGGER.warn("part_ordering.json: Unknown attachment point: " + entry.getKey());
			else {
				final List<TResourceLocation> realValues = entry.getValue()
						.stream()
						.map(str -> {
							try {
								return TailsPlatform.get().parseResourceLocation(Objects.requireNonNull(str));
							} catch (Exception e) {
								Tails.LOGGER.warn("part_ordering.json: Invalid resource location '{}'!\n{}", str, e.toString());
								return null;
							}
						})
						.filter(rl -> rl != null)
						.toList();

				ordering.put(attachment, realValues);
			}
		}

		return ordering;
	}

	/**
	 * Performs a reload, using the given resource manager to access resources.
	 * @param manager The resource manager.
	 * @return The fully-baked list of parts.
	 */
	private List<Part> reloadParts(ResourceManagerWrapper manager) {
		var resources = manager.listJsonFiles("tails/parts", rl -> rl.t$getPath().endsWith(".json"));

		final List<ResourcePair> parts = new ArrayList<>();

		for (Map.Entry<TResourceLocation, JsonElement> entry : resources.entrySet())
			parts.add(new ResourcePair(entry.getKey(), entry.getValue()));

		final List<ResourcePair> subTypes = new ArrayList<>();
		resources = manager.listJsonFiles("tails/subtypes", rl -> rl.t$getPath().endsWith(".json"));

		for (Map.Entry<TResourceLocation, JsonElement> entry : resources.entrySet())
			subTypes.add(new ResourcePair(entry.getKey(), entry.getValue()));

		final List<ResourcePair> textures = new ArrayList<>();
		final List<ResourcePair> orderings = new ArrayList<>();
		resources = manager.listJsonFiles("tails/part_textures", rl -> rl.t$getPath().endsWith(".json"));

		for (Map.Entry<TResourceLocation, JsonElement> entry : resources.entrySet()) {
			final String path = entry.getKey().t$getPath();
			final ResourcePair pair = new ResourcePair(entry.getKey(), entry.getValue());

			if (path.endsWith("/ordering.json"))
				orderings.add(pair);
			else textures.add(pair);
		}

		final Map<TResourceLocation, List<String>> realOrderings = new TreeMap<>();

		for (ResourcePair pair : orderings) {
			final JsonElement json = pair.json();
			if (json == null) continue;
			if (!json.isJsonArray()) {
				Tails.LOGGER.warn("Texture ordering {} must be a json array!", pair.location());
				continue;
			}

			final JsonArray arr = json.getAsJsonArray();
			final List<String> values = new ArrayList<>();
			for (int i = 0; i < arr.size(); i++)
				values.add(arr.get(i).getAsString());

			String id = trim(pair.location().t$getPath(), "tails/part_textures/");
			id = id.substring(0, id.length() - "/ordering".length());

			realOrderings.put(pair.location().t$withPath(id), values);
		}

		if (DEBUG_REGISTRIES)
			Tails.LOGGER.info("Texture orderings ({}):\n{}", realOrderings.size(), realOrderings.entrySet().stream()
					.map(e -> e.getKey() + " = " + e.getValue())
					.collect(Collectors.joining("\n")));

		final List<NamedTexture> realTextures = new ArrayList<>(textures.size());

		for (ResourcePair pair : textures) {
			final JsonElement json = pair.json();
			if (json == null) continue;
			if (!json.isJsonObject()) {
				Tails.LOGGER.warn("Texture {} must be a json object!", pair.location());
				continue;
			}

			final NamedTexture tex = readTexture(pair.location(), json.getAsJsonObject());
			realTextures.add(tex);

			if (tex.applyTo().isEmpty())
				Tails.LOGGER.warn("Texture {} does not apply to any sub types!", pair.location());
		}

		if (DEBUG_REGISTRIES)
			Tails.LOGGER.info("Textures ({}):\n{}", realTextures.size(), realTextures.stream()
					.map(NamedTexture::toString)
					.collect(Collectors.joining("\n")));

		final List<NamedSubType> realSubTypes = new ArrayList<>(subTypes.size());

		for (ResourcePair pair : subTypes) {
			final JsonElement json = pair.json();
			if (json == null) continue;
			if (!json.isJsonObject()) {
				Tails.LOGGER.warn("Sub type {} must be a json object!", pair.location());
				continue;
			}

			final NamedSubType subType = readSubType(pair.location(), json.getAsJsonObject(), realTextures, realOrderings);

			if (subType.subType().textures().isEmpty())
				Tails.LOGGER.error("Sub type {} is missing any texture definitions! Skipping!", pair.location());
			else
				realSubTypes.add(subType);
		}

		if (DEBUG_REGISTRIES)
			Tails.LOGGER.info("Sub types ({}):\n{}", realSubTypes.size(), realSubTypes.stream()
					.map(NamedSubType::toString)
					.collect(Collectors.joining("\n")));

		final List<Part> realParts = new ArrayList<>();

		for (ResourcePair pair : parts) {
			final JsonElement json = pair.json();
			if (json == null) continue;
			if (!json.isJsonObject()) {
				Tails.LOGGER.warn("Part {} must be a json object!", pair.location());
				continue;
			}

			final Part part;
			try {
				part = readPart(pair.location(), json.getAsJsonObject(), realSubTypes);
			} catch (Exception e) {
				Tails.LOGGER.error("Failed to read part {}:", pair.location(), e);
				continue;
			}

			if (part.getSubTypes().isEmpty())
				Tails.LOGGER.error("Part {} is missing any sub types! Skipping!", pair.location());
			else
				realParts.add(part);
		}

		if (DEBUG_REGISTRIES) {
			TailsPlatform.get().logInfo("Parts ({}):\n{}", realParts.size(), realParts.stream()
					.map(Part::toString)
					.collect(Collectors.joining("\n")));

			final String out = realParts.stream()
					.map(part -> part.getId() + "\n  = " + part.getSubTypes().stream()
							.map(sb -> sb.id() + "\n    - " + sb.textures().stream()
									.map(tex -> tex.id())
									.collect(Collectors.joining("\n    - ")))
							.collect(Collectors.joining("\n  = ")))
					.collect(Collectors.joining("\n"));
			TailsPlatform.get().logInfo("Part dependency graph:\n{}", out);

			TailsPlatform.get().logInfo("Attachment point roots: {}", AttachmentPoints.getRoots());
			TailsPlatform.get().logInfo("Attachment points: {}", AttachmentPoints.getAll());
		}

		return realParts;
	}

	/**
	 * Parses the part described by the given json.
	 * @param location The location of the part.
	 * @param json The json contents of the part.
	 * @param subTypes The subtypes of the part.
	 * @return The part.
	 */
	private static Part readPart(TResourceLocation location, JsonObject json, List<NamedSubType> subTypes) {
		final String id = trim(location.t$getPath(), "tails/parts/");
		final TResourceLocation realId = location.t$withPath(id);

		final AttachmentPoint attachment = AttachmentPoints.getOrCreate(TailsGsonHelper.getAsString(json, "attachment"));
		if (attachment == null) throw new JsonParseException(location + ": missing attachment!");

		final int[] tints;
		if (json.has("defaultTints")) {
			final JsonArray arr = TailsGsonHelper.getAsJsonArray(json, "defaultTints");
			tints = new int[] {
					hex(TailsGsonHelper.convertToString(arr.get(0), "defaultTints[0]")),
					hex(TailsGsonHelper.convertToString(arr.get(1), "defaultTints[1]")),
					hex(TailsGsonHelper.convertToString(arr.get(2), "defaultTints[2]"))
			};
		}
		else tints = null;

		final List<String> ordering = json.has("ordering") ? getAsStringArray(json, "ordering") : List.of();

		final List<Part.SubType> subs = order(realId, ordering, subTypes, (subType, ord) -> subType.unwrap().id().equals(ord));

		final TailsModelPart model = json.has("model") ? ModelSerializer.deserializeRoot(TailsGsonHelper.getAsJsonObject(json, "model")).bake() : null;

		return new Part(realId, attachment, subs, tints,
				TailsGsonHelper.getAsBoolean(json, "allowArrows", false), model,
				json.has("render") ? readTransform(TailsGsonHelper.getAsJsonObject(json, "render")) : Transformation.ZERO,
				json.has("preview") ? readTransform(TailsGsonHelper.getAsJsonObject(json, "preview")) : Transformation.ZERO);
	}

	private static Transformation readTransform(JsonObject obj) {
		final Vector3fc scale = obj.has("scale") ? readVector(TailsGsonHelper.getAsJsonArray(obj, "scale"), "scale") : Transformation.ZERO_VECTOR;
		final Vector3fc offset = obj.has("offset") ? readVector(TailsGsonHelper.getAsJsonArray(obj, "offset"), "offset") : Transformation.ZERO_VECTOR;
		final Vector3fc rotation = obj.has("rotation") ? readVector(TailsGsonHelper.getAsJsonArray(obj, "rotation"), "rotation") : Transformation.ZERO_VECTOR;
		return new Transformation(scale, offset, rotation);
	}

	private static Vector3f readVector(JsonArray array, String name) {
		return new Vector3f(
				TailsGsonHelper.convertToFloat(array.get(0), name + "[0]"),
				TailsGsonHelper.convertToFloat(array.get(1), name + "[1]"),
				TailsGsonHelper.convertToFloat(array.get(2), name + "[2]")
				);
	}

	/**
	 * Parses the subtype described by the given json.
	 * @param location The location of the subtype.
	 * @param json The json contents of the subtype.
	 * @param textures The textures that apply to the subtype.
	 * @param textureOrderings The list of texture orderings.
	 * @return The subtype.
	 */
	private static NamedSubType readSubType(TResourceLocation location, JsonObject json, List<NamedTexture> textures, Map<TResourceLocation, List<String>> textureOrderings) {
		final String id = trim(location.t$getPath(), "tails/subtypes/");

		final String partPath = id.substring(0, id.lastIndexOf('/'));
		final TResourceLocation partId = location.t$withPath(partPath);

		final String typeId = id.substring(partPath.length() + 1);

		final String author = json.has("author") ? TailsGsonHelper.getAsString(json, "author") : null;
		final Transformation transforms = json.has("pose") ? readTransform(TailsGsonHelper.getAsJsonObject(json, "pose")) : Transformation.ZERO;
		final List<PartPath> hideParts = json.has("hideParts") ? getAsStringArray(json, "hideParts").stream().map(PartPath::new).toList() : List.of();
		final List<PartPath> showParts = json.has("showParts") ? getAsStringArray(json, "showParts").stream().map(PartPath::new).toList() : List.of();

		final List<NamedTexture> tex = textures.stream()
				.filter(tx -> tx.partId().equals(partId) && tx.applyTo().contains(typeId))
				.toList();

		final List<String> ordering = textureOrderings.getOrDefault(partId, List.of());

		final List<Part.PartTexture> newTex = order(partId, ordering, tex, (t, ord) -> t.unwrap().id().equals(ord));

		return new NamedSubType(partId, new Part.SubType(typeId, author, transforms, hideParts, showParts, newTex));
	}

	/**
	 * Parses the texture described by the given json.
	 * @param location The location of the texture.
	 * @param json The json contents of the texture.
	 * @return The texture.
	 */
	private static NamedTexture readTexture(TResourceLocation location, JsonObject json) {
		final String id = trim(location.t$getPath(), "tails/part_textures/");

		final String partPath = id.substring(0, id.lastIndexOf('/'));
		final TResourceLocation partId = location.t$withPath(partPath);

		final String texId = id.substring(partPath.length() + 1);

		String path = json.has("path") ? TailsGsonHelper.getAsString(json, "path") : partId.t$getPath() + "/" + texId;
		path = "textures/part/" + path + ".png";
		final String author = json.has("author") ? TailsGsonHelper.getAsString(json, "author") : null;

		final List<String> applyTo = new ArrayList<>();

		if (json.has("applyTo"))
			applyTo.addAll(getAsStringArray(json, "applyTo"));

		Part.TintingStrategy tintingStrategy = Part.TintingStrategy.TRIPLE_TINT;

		if (json.has("tintingStrategy")) {
			final String strat = TailsGsonHelper.getAsString(json, "tintingStrategy");
			tintingStrategy = Part.TintingStrategy.of(strat);
			if (tintingStrategy == null) {
				tintingStrategy = Part.TintingStrategy.TRIPLE_TINT;
				Tails.LOGGER.warn("{}: Invalid tinting strategy: {}!", location, strat);
			}
		}

		return new NamedTexture(partId, List.copyOf(applyTo), new Part.PartTexture(texId, path, author, tintingStrategy));
	}

	/**
	 * Attempts to parse the specified json member as a string array.
	 * @param obj The object containing the array.
	 * @param name The name of the array.
	 * @return The contents of the string array.
	 */
	public static List<String> getAsStringArray(JsonObject obj, String name) {
		final List<String> ret = new ArrayList<>();

		if (obj.get(name) instanceof JsonArray arr)
			for (int i = 0; i < arr.size(); i++)
				ret.add(TailsGsonHelper.convertToString(arr.get(i), name + "[" + i + "]"));

		return ret;
	}

	/**
	 * Parses the given hexadecimal input into the corresponding integer.
	 * @param input The input to parse.
	 * @return The integer.
	 * @throws NumberFormatException
	 */
	private static int hex(String input) {
		input = input.toLowerCase(Locale.ENGLISH);
		if (input.startsWith("0x"))
			input = input.substring(2);
		return Integer.parseInt(input, 16);
	}

	/**
	 * Trims off the given beginning string from the input string and ".json" from the end.
	 * @param input The input string.
	 * @param beginning The beginning string.
	 * @return The trimmed string.
	 */
	private static String trim(String input, String beginning) {
		input = input.substring(beginning.length());
		return input.substring(0, input.length() - ".json".length());
	}

	private static <V, T extends Named<V>> List<V> order(TResourceLocation id, List<String> ordering, List<T> all, BiPredicate<T, String> orderMatcher) {
		final List<T> applicable = all.stream()
				.filter(n -> n.id().equals(id))
				.sorted(Comparator.comparing(Named::id, TResourceLocation::t$compareNamespaced))
				.toList();

		final List<V> resolved = ordering.stream()
				.map(ord -> applicable.stream()
						.filter(v -> orderMatcher.test(v, ord))
						.findFirst()
						.orElse(null))
				.filter(t -> t != null)
				.map(Named::unwrap)
				.collect(Collectors.toList());

		// Add unordered ones to the end of the list in alphabetical order.
		for (Named<V> named : applicable)
			if (!resolved.contains(named.unwrap()))
				resolved.add(named.unwrap());

		return List.copyOf(resolved);
	}

	private static interface Named<T> {
		public TResourceLocation id();
		public T unwrap();
	}

	private static record NamedSubType(TResourceLocation partId, Part.SubType subType) implements Named<Part.SubType> {
		@Override
		public TResourceLocation id() { return partId; }
		@Override
		public Part.SubType unwrap() { return subType; }
	}

	private static record NamedTexture(TResourceLocation partId, List<String> applyTo, Part.PartTexture texture) implements Named<Part.PartTexture> {
		@Override
		public TResourceLocation id() { return partId; }
		@Override
		public Part.PartTexture unwrap() { return texture; }
	}

	private static record ResourcePair(TResourceLocation location, JsonElement json) {
		@Override
		public String toString() {
			return location.toString();
		}
	}
}