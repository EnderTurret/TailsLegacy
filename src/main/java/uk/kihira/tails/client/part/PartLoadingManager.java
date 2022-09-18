package uk.kihira.tails.client.part;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartType;

@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class PartLoadingManager implements ResourceManagerReloadListener {

	private final Runnable clear;
	private final BiConsumer<List<Part>, Map<PartType, List<ResourceLocation>>> onComplete;

	PartLoadingManager(Runnable clear, BiConsumer<List<Part>, Map<PartType, List<ResourceLocation>>> onComplete) {
		this.clear = clear;
		this.onComplete = onComplete;
	}

	@SubscribeEvent
	static void registerReloadListeners(RegisterClientReloadListenersEvent e) {
		e.registerReloadListener(PartRegistry.MANAGER);
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		clear.run();

		final List<Part> parts = new ArrayList<>();

		try {
			parts.addAll(reload(manager));
		} catch (Exception e) {
			Tails.LOGGER.fatal("Critical part loading failure!", e);
		}

		final Map<PartType, List<ResourceLocation>> ordering = new EnumMap<>(PartType.class);

		try {
			ordering.putAll(readOrdering(manager));
		} catch (Exception e) {
			Tails.LOGGER.error("Failed to apply part ordering!", e);
		}

		onComplete.accept(parts, ordering);
	}

	private static final Type ORDERING_TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();

	private Map<PartType, List<ResourceLocation>> readOrdering(ResourceManager manager) {
		final Map<PartType, List<ResourceLocation>> ordering = new EnumMap<>(PartType.class);

		final ResourceLocation loc = new ResourceLocation(Tails.MOD_ID, "part_ordering.json");
		final Resource res = manager.getResource(loc).get();
		final JsonElement json = readJson(loc, res);
		if (json == null) return Map.of();

		final Map<String, List<String>> rawOrdering = Tails.GSON.fromJson(json, ORDERING_TYPE);

		for (Map.Entry<String, List<String>> entry : rawOrdering.entrySet()) {
			final PartType type = PartType.forId(entry.getKey());
			if (type == null)
				Tails.LOGGER.warn("part_ordering.json: Unknown part type: " + entry.getKey());
			else {
				final List<ResourceLocation> realValues = entry.getValue()
						.stream()
						.map(str -> {
							try {
								return new ResourceLocation(str);
							} catch (ResourceLocationException e) {
								Tails.LOGGER.warn("part_ordering.json: Invalid resource location '{}'!\n{}", str, e.toString());
								return null;
							}
						})
						.filter(rl -> rl != null)
						.toList();

				ordering.put(type, realValues);
			}
		}

		return ordering;
	}

	private List<Part> reload(ResourceManager manager) {
		final var resources = manager.listResources("parts", rl -> rl.getPath().endsWith(".json"));

		final List<ResourcePair> parts = new ArrayList<>();
		final List<ResourcePair> subTypes = new ArrayList<>();
		final List<ResourcePair> textures = new ArrayList<>();
		final List<ResourcePair> orderings = new ArrayList<>();

		for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
			final String path = entry.getKey().getPath();
			final ResourcePair pair = new ResourcePair(entry.getKey(), entry.getValue());

			if (path.contains("parts/subtypes"))
				subTypes.add(pair);
			else if (path.contains("parts/textures")) {
				if (path.endsWith("/ordering.json"))
					orderings.add(pair);
				else textures.add(pair);
			} else
				parts.add(pair);
		}

		Tails.LOGGER.info("Parts: {}", parts);
		Tails.LOGGER.info("Sub types: {}", subTypes);
		Tails.LOGGER.info("Textures: {}", textures);
		Tails.LOGGER.info("Orderings: {}", orderings);
		Tails.LOGGER.info("--------------------------------------------");

		final Map<ResourceLocation, List<String>> realOrderings = new HashMap<>();

		for (ResourcePair pair : orderings) {
			final JsonElement json = readJson(pair.location(), pair.resource());
			if (json == null) continue;
			if (!json.isJsonArray()) {
				Tails.LOGGER.warn("Texture ordering {} must be a json array!", pair.location());
				continue;
			}

			final JsonArray arr = json.getAsJsonArray();
			final List<String> values = new ArrayList<>();
			for (int i = 0; i < arr.size(); i++)
				values.add(arr.get(i).getAsString());

			String id = trim(pair.location().getPath(), "parts/textures/");
			id = id.substring(0, id.length() - "/ordering".length());

			realOrderings.put(new ResourceLocation(pair.location().getNamespace(), id), values);
		}

		Tails.LOGGER.info("Orderings: {}", realOrderings);

		final List<NamedTexture> realTextures = new ArrayList<>(textures.size());

		for (ResourcePair pair : textures) {
			final JsonElement json = readJson(pair.location(), pair.resource());
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

		Tails.LOGGER.info("Textures: {}", realTextures);

		final List<NamedSubType> realSubTypes = new ArrayList<>(subTypes.size());

		for (ResourcePair pair : subTypes) {
			final JsonElement json = readJson(pair.location(), pair.resource());
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

		Tails.LOGGER.info("Sub types: {}", realSubTypes);

		final List<Part> realParts = new ArrayList<>();

		for (ResourcePair pair : parts) {
			final JsonElement json = readJson(pair.location(), pair.resource());
			if (json == null) continue;
			if (!json.isJsonObject()) {
				Tails.LOGGER.warn("Part {} must be a json object!", pair.location());
				continue;
			}

			final Part part = readPart(pair.location(), json.getAsJsonObject(), realSubTypes);

			if (part.getSubTypes().isEmpty())
				Tails.LOGGER.error("Part {} is missing any sub types! Skipping!", pair.location());
			else
				realParts.add(part);
		}

		Tails.LOGGER.info("Parts: {}", realParts);

		return realParts;
	}

	private static Part readPart(ResourceLocation location, JsonObject json, List<NamedSubType> subTypes) {
		final String id = trim(location.getPath(), "parts/");
		final ResourceLocation realId = new ResourceLocation(location.getNamespace(), id);

		final PartType category = PartType.forId(json.get("category").getAsString());
		if (category == null) throw new JsonParseException(location + ": missing category!");

		final int[] tints;
		if (json.has("defaultTints")) {
			final JsonArray arr = json.get("defaultTints").getAsJsonArray();
			tints = new int[] { hex(arr.get(0).getAsString()), hex(arr.get(1).getAsString()), hex(arr.get(2).getAsString()) };
		}
		else tints = null;

		final List<String> ordering = json.has("ordering") ? readStringArray(json.get("ordering")) : List.of();

		final List<Part.SubType> applicable = subTypes.stream()
				.filter(nst -> nst.partId().equals(realId))
				.map(NamedSubType::subType)
				.toList();

		final List<Part.SubType> subs = ordering.stream()
				.map(i -> applicable.stream().filter(st -> st.id().equals(i)).findFirst().orElse(null))
				.filter(st -> st != null)
				.collect(Collectors.toList());

		for (Part.SubType sub : applicable)
			if (!subs.contains(sub))
				subs.add(sub);

		return new Part(realId, category, List.copyOf(subs), tints);
	}

	private static NamedSubType readSubType(ResourceLocation location, JsonObject json, List<NamedTexture> textures, Map<ResourceLocation, List<String>> textureOrderings) {
		final String id = trim(location.getPath(), "parts/subtypes/");

		final String partPath = id.substring(0, id.lastIndexOf('/'));
		final ResourceLocation partId = new ResourceLocation(location.getNamespace(), partPath);

		final String typeId = id.substring(partPath.length() + 1);

		final String author = json.has("author") ? json.get("author").getAsString() : null;

		final List<Part.PartTexture> tex = textures.stream()
				.filter(tx -> tx.partId().equals(partId) && tx.applyTo().contains(typeId))
				.map(NamedTexture::texture)
				.toList();

		final List<String> ordering = textureOrderings.getOrDefault(partId, List.of());

		final List<Part.PartTexture> newTex = ordering.stream()
				.map(i -> tex.stream().filter(pt -> pt.id().equals(i)).findFirst().orElse(null))
				.filter(t -> t != null)
				.collect(Collectors.toList());

		for (Part.PartTexture t : tex)
			if (!newTex.contains(t))
				newTex.add(t);

		return new NamedSubType(partId, new Part.SubType(typeId, author, newTex));
	}

	private static NamedTexture readTexture(ResourceLocation location, JsonObject json) {
		final String id = trim(location.getPath(), "parts/textures/");

		final String partPath = id.substring(0, id.lastIndexOf('/'));
		final ResourceLocation partId = new ResourceLocation(location.getNamespace(), partPath);

		final String texId = id.substring(partPath.length() + 1);

		final String path = json.has("path") ? "textures/" + json.get("path").getAsString() + ".png" : "textures/part/" + partId.getPath() + "/" + texId + ".png";
		final String author = json.has("author") ? json.get("author").getAsString() : null;

		final List<String> applyTo = new ArrayList<>();

		if (json.has("applyTo"))
			applyTo.addAll(readStringArray(json.get("applyTo")));

		return new NamedTexture(partId, List.copyOf(applyTo), new Part.PartTexture(texId, path, author));
	}

	private static List<String> readStringArray(JsonElement elem) {
		final List<String> ret = new ArrayList<>();

		if (elem.isJsonArray()) {
			final JsonArray arr = elem.getAsJsonArray();
			for (int i = 0; i < arr.size(); i++)
				ret.add(arr.get(i).getAsString());
		}

		return ret;
	}

	private static int hex(String input) {
		input = input.toLowerCase(Locale.ENGLISH);
		if (input.startsWith("0x"))
			input = input.substring(2);
		return Integer.parseInt(input, 16);
	}

	private static String trim(String input, String beginning) {
		input = input.substring(beginning.length());
		return input.substring(0, input.length() - ".json".length());
	}

	@Nullable
	private static JsonElement readJson(ResourceLocation location, Resource resource) {
		try (BufferedReader br = resource.openAsReader()) {
			return JsonParser.parseReader(br);
		} catch (Exception e) {
			// The stack trace might be increasingly large, so try not to log it.
			Tails.LOGGER.warn("Failed to read json file {}:\n{}", location, e.toString());
			return null;
		}
	}

	private static record NamedSubType(ResourceLocation partId, Part.SubType subType) {}

	private static record NamedTexture(ResourceLocation partId, List<String> applyTo, Part.PartTexture texture) {}

	private static record ResourcePair(ResourceLocation location, Resource resource) {
		@Override
		public String toString() {
			return location.toString();
		}
	}
}