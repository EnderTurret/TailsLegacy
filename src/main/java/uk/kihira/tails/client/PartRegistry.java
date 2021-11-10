/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.model.MuzzleModel;
import uk.kihira.tails.client.model.ears.CatEarsModel;
import uk.kihira.tails.client.model.ears.FoxEarsModel;
import uk.kihira.tails.client.model.ears.PandaEarsModel;
import uk.kihira.tails.client.model.ears.SmallCatEarsModel;
import uk.kihira.tails.client.model.tail.BirdTailModel;
import uk.kihira.tails.client.model.tail.BunnyTailModel;
import uk.kihira.tails.client.model.tail.CatTailModel;
import uk.kihira.tails.client.model.tail.DevilTailModel;
import uk.kihira.tails.client.model.tail.DragonTailModel;
import uk.kihira.tails.client.model.tail.FluffyTailModel;
import uk.kihira.tails.client.model.tail.RaccoonTailModel;
import uk.kihira.tails.client.model.tail.SharkTailModel;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.client.render.SeaPickleRenderer;
import uk.kihira.tails.client.render.WingRenderer;
import uk.kihira.tails.common.part.PartType;

// Yeah using OnlyIn isn't nice but as this is static, it means it only gets constructed on the uk.kihira.tails.client.
@OnlyIn(Dist.CLIENT)
public class PartRegistry {

	private static final ArrayListMultimap<PartType, PartRenderer> PART_REGISTRY = ArrayListMultimap.create();

	static {
		// Tails
		register(new PartRenderer(PartType.TAIL, "fluffy", 2, new FluffyTailModel(), null, "fox_tail"));
		register(new PartRenderer(PartType.TAIL, "dragon", 1, new DragonTailModel(), null, "dragon_tail", "dragon_tail_striped")
				.setAuthor("@TTFTCUTS", 0, 0).setAuthor("@TTFTCUTS", 1, 0));
		register(new PartRenderer(PartType.TAIL, "raccoon", 0, new RaccoonTailModel(), null, "racoon_tail"));
		register(new PartRenderer(PartType.TAIL, "devil", 1, new DevilTailModel(), null, "devil_tail"));
		register(new PartRenderer(PartType.TAIL, "cat", 0, new CatTailModel(), null, "tabby_tail", "tiger_tail"));
		register(new PartRenderer(PartType.TAIL, "bird", 0, new BirdTailModel(), null, "bird_tail")
				.setAuthor("@blusunrize", 0, 0));
		register(new PartRenderer(PartType.TAIL, "shark", 0, new SharkTailModel(), "access_denied", "shark_tail"));
		register(new PartRenderer(PartType.TAIL, "bunny", 0, new BunnyTailModel(), "@carrotcodes", "bunny_tail"));

		// Ears
		register(new PartRenderer(PartType.EARS, "fox", 1, new FoxEarsModel(), "@Adeon", "fox_ears"));
		register(new PartRenderer(PartType.EARS, "cat", 0, new CatEarsModel(), null, "cat_ears"));
		register(new PartRenderer(PartType.EARS, "panda", 0, new PandaEarsModel(), null, "panda_ears"));
		register(new PartRenderer(PartType.EARS, "cat_small", 0, new SmallCatEarsModel(), null, "cat_small_ears"));
		register(new SeaPickleRenderer(PartType.EARS, "sea_pickle", 0, null, null, "sea_pickle"));

		// Wings
		register(new WingRenderer(PartType.WINGS, "big", 1, null, null, "big_wings", "metal_wings", "dragon_wings", "dragon_boneless_wings")
				.setAuthor("@littlechippie").setAuthor("Dracyoshi", 0, 2).setAuthor("Dracyoshi", 0, 3).setAuthor("Dracyoshi", 1, 2).setAuthor("Dracyoshi", 1, 3));

		// Muzzle
		register(new PartRenderer(PartType.MUZZLE, "standard", 4, new MuzzleModel(-2f, -3f, -9f, 4, 3, 5), null, "standard_muzzle", "alt_muzzle"));
		register(new PartRenderer(PartType.MUZZLE, "slim", 4, new MuzzleModel(-2f, -2f, -9f, 4, 2, 5), null, "standard_muzzle", "alt_muzzle"));
		register(new PartRenderer(PartType.MUZZLE, "thin", 4, new MuzzleModel(-1.5f, -2f, -9f, 3, 2, 5, 0, 9), null, "standard_muzzle", "alt_muzzle"));
	}

	/**
	 * Adds the given renderer to the registry.
	 * @param part The part to register.
	 */
	public static void register(PartRenderer part) {
		PART_REGISTRY.put(part.getType(), part);
	}

	/**
	 * Returns a list of {@link PartRenderer PartRenderers} under the given type.
	 * @param partType The desired type of the renderers.
	 * @return The list.
	 */
	public static List<PartRenderer> getParts(PartType partType) {
		return PART_REGISTRY.get(partType);
	}

	/**
	 * Returns the renderer at the given index for the given type.<br>
	 * If the index is out of bounds, it's normalized to {@code 0}.
	 * @param partType The part type.
	 * @param index The type id.
	 * @return The part renderer.
	 */
	public static PartRenderer getPartRenderer(PartType partType, int index) {
		final List<PartRenderer> parts = PartRegistry.getParts(partType);

		index = index >= parts.size() ? 0 : index;

		return parts.get(index);
	}
}
