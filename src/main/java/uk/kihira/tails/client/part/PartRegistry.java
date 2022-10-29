/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.Parts;

public final class PartRegistry {

	private static final Map<ResourceLocation, Part> PART_REGISTRY = new TreeMap<>();
	private static final ListMultimap<PartType, Part> BY_TYPE = MultimapBuilder.enumKeys(PartType.class).arrayListValues().build();

	static final PartLoadingManager MANAGER = new PartLoadingManager(() -> {
		PART_REGISTRY.clear();
		BY_TYPE.clear();
	}, (parts, ordering) -> {
		for (Part part : parts)
			PART_REGISTRY.put(part.getId(), part);

		for (Map.Entry<PartType, List<ResourceLocation>> entry : ordering.entrySet()) {
			final List<Part> list = entry.getValue().stream()
					.map(PART_REGISTRY::get)
					.filter(p -> p != null)
					.collect(Collectors.toList());

			for (Part part : parts)
				if (part.getType() == entry.getKey() && !list.contains(part))
					list.add(part);

			BY_TYPE.putAll(entry.getKey(), List.copyOf(list));
		}

		LocalPartManager.reload();
		Tails.PROXY.getLibraryManager().reload(false);
	});

	public static final PartReference FLUFFY_TAIL = reference("tail/fluffy_tail");
	public static final PartReference DRAGON_TAIL = reference("tail/dragon_tail");
	public static final PartReference RACCOON_TAIL = reference("tail/raccoon_tail");
	public static final PartReference DEVIL_TAIL = reference("tail/devil_tail");
	public static final PartReference CAT_TAIL = reference("tail/cat_tail");
	public static final PartReference BIRD_TAIL = reference("tail/bird_tail");
	public static final PartReference SHARK_TAIL = reference("tail/shark_tail");
	public static final PartReference BUNNY_TAIL = reference("tail/bunny_tail");

	public static final PartReference FOX_EARS = reference("ears/fox_ears");
	public static final PartReference CAT_EARS = reference("ears/cat_ears");
	public static final PartReference PANDA_EARS = reference("ears/panda_ears");
	public static final PartReference SMALL_CAT_EARS = reference("ears/small_cat_ears");
	public static final PartReference SEA_PICKLE = reference("ears/sea_pickle");
	public static final PartReference ANTENNAE = reference("ears/antennae");
	public static final PartReference DEMON_HORNS = reference("ears/demon_horns");

	public static final PartReference BIG_WINGS = reference("wings/big_wings");

	public static final PartReference STANDARD_MUZZLE = reference("muzzle/standard_muzzle");
	public static final PartReference SLIM_MUZZLE = reference("muzzle/slim_muzzle");
	public static final PartReference THIN_MUZZLE = reference("muzzle/thin_muzzle");

	public static PartReference reference(ResourceLocation id) {
		return new PartReference(id);
	}

	public static PartReference reference(String id) {
		return reference(new ResourceLocation(Tails.MOD_ID, id));
	}

	public static Part get(ResourceLocation id) {
		return PART_REGISTRY.get(id);
	}

	/**
	 * Returns a list of {@link Part Parts} under the given type.
	 * @param partType The desired type of the renderers.
	 * @return The list.
	 */
	public static List<Part> getParts(PartType partType) {
		return BY_TYPE.get(partType);
	}

	/**
	 * Returns the part at the given index for the given type.<br>
	 * If the index is out of bounds, it's normalized to {@code 0}.
	 * @param partType The part type.
	 * @param index The type id.
	 * @return The part renderer.
	 */
	public static Part byLegacyId(PartType partType, int index) {
		return get(Parts.byLegacyId(partType, index));
	}

	public static final class PartReference implements Supplier<Part> {

		private final ResourceLocation id;

		PartReference(ResourceLocation id) {
			this.id = id;
		}

		public ResourceLocation id() {
			return id;
		}

		@Override
		public Part get() {
			return PartRegistry.get(id);
		}
	}
}