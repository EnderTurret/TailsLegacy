/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import static uk.kihira.tails.common.part.Part.Builder.ears;
import static uk.kihira.tails.common.part.Part.Builder.muzzle;
import static uk.kihira.tails.common.part.Part.Builder.tail;
import static uk.kihira.tails.common.part.Part.Builder.wings;
import static uk.kihira.tails.common.part.Part.Builder.head;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;

import net.minecraft.util.ResourceLocation;

public class PartRegistry {

	private static final Map<ResourceLocation, Part> PART_REGISTRY = new HashMap<>();
	private static final ArrayListMultimap<PartType, Part> BY_TYPE = ArrayListMultimap.create();

	public static final Part FLUFFY_TAIL;
	public static final Part DRAGON_TAIL;
	public static final Part RACCOON_TAIL;
	public static final Part DEVIL_TAIL;
	public static final Part CAT_TAIL;
	public static final Part BIRD_TAIL;
	public static final Part SHARK_TAIL;
	public static final Part BUNNY_TAIL;

	public static final Part FOX_EARS;
	public static final Part CAT_EARS;
	public static final Part PANDA_EARS;
	public static final Part SMALL_CAT_EARS;

	public static final Part BIG_WINGS;

	public static final Part STANDARD_MUZZLE;
	public static final Part SLIM_MUZZLE;
	public static final Part THIN_MUZZLE;

	public static final Part SEA_PICKLE;

	static {
		// Tails
		FLUFFY_TAIL = tail("fluffy_tail").subType().subType().register();
		DRAGON_TAIL = tail("dragon_tail").subType().texture("dragon_tail_striped")
				.author("@TTFTCUTS", 0).author("@TTFTCUTS", 1).register();
		RACCOON_TAIL = tail("raccoon_tail").register();
		DEVIL_TAIL = tail("devil_tail").subType().register();
		CAT_TAIL = tail("cat_tail").texture("tiger_tail").register();
		BIRD_TAIL = tail("bird_tail").author("@blusunrize", 0).register();
		SHARK_TAIL = tail("shark_tail").author("access_denied", 0).register();
		BUNNY_TAIL = tail("bunny_tail").author("@carrotcodes", 0).register();

		// Ears
		FOX_EARS = ears("fox_ears").subType().author("@Adeon").register();
		CAT_EARS = ears("cat_ears").register();
		PANDA_EARS = ears("panda_ears").register();
		SMALL_CAT_EARS = ears("small_cat_ears").register();

		// Wings
		BIG_WINGS = wings("big_wings").subType().texture("metal_wings", "dragon_wings", "dragon_boneless_wings")
				.author("@littlechippie")
				.author("Dracyoshi", 0, 2)
				.author("Dracyoshi", 0, 3)
				.author("Dracyoshi", 1, 2)
				.author("Dracyoshi", 1, 3).register();

		// Muzzle
		STANDARD_MUZZLE = muzzle("standard_muzzle").subType().subType().subType().subType().texture("alt_muzzle").register();
		SLIM_MUZZLE = muzzle("slim_muzzle").subType().subType().subType().subType().texture("alt_muzzle").register();
		THIN_MUZZLE = muzzle("thin_muzzle").subType().subType().subType().subType().texture("alt_muzzle").register();

		SEA_PICKLE = head("sea_pickle").register(SeaPicklePart::new);
	}

	/**
	 * Adds the given part to the registry.
	 * @param part The part to register.
	 */
	public static void register(Part part) {
		PART_REGISTRY.put(part.getId(), part);
		BY_TYPE.put(part.getType(), part);
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
	public static Part byNumericId(PartType partType, int index) {
		switch (partType) {
		case TAIL:
			switch (index) {
			case 0: return FLUFFY_TAIL;
			case 1: return DRAGON_TAIL;
			case 2: return RACCOON_TAIL;
			case 3: return DEVIL_TAIL;
			case 4: return CAT_TAIL;
			case 5: return BIRD_TAIL;
			case 6: return SHARK_TAIL;
			case 7: return BUNNY_TAIL;
			default: return FLUFFY_TAIL;
			}
		case EARS:
			switch (index) {
			case 0: return FOX_EARS;
			case 1: return CAT_EARS;
			case 2: return PANDA_EARS;
			case 3: return SMALL_CAT_EARS;
			case 4: return SEA_PICKLE;
			default: return FOX_EARS;
			}
		case MUZZLE:
			switch (index) {
			case 0: return STANDARD_MUZZLE;
			case 1: return SLIM_MUZZLE;
			case 2: return THIN_MUZZLE;
			default: return STANDARD_MUZZLE;
			}
		case WINGS: return BIG_WINGS;
		default:
			throw new IllegalArgumentException("Unhandled part type: " + partType);
		}
	}
}
