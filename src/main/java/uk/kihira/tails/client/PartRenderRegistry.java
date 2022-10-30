/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import static uk.kihira.tails.client.part.PartRegistry.*;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.model.MuzzleModel;
import uk.kihira.tails.client.model.ears.AntennaeModel;
import uk.kihira.tails.client.model.ears.AxolotlGillsModel;
import uk.kihira.tails.client.model.ears.CatEarsModel;
import uk.kihira.tails.client.model.ears.DeerAntlersModel;
import uk.kihira.tails.client.model.ears.DemonHornsModel;
import uk.kihira.tails.client.model.ears.FoxEarsModel;
import uk.kihira.tails.client.model.ears.PandaEarsModel;
import uk.kihira.tails.client.model.ears.SeaPickleModel;
import uk.kihira.tails.client.model.ears.SmallCatEarsModel;
import uk.kihira.tails.client.model.ears.StriderWhiskersModel;
import uk.kihira.tails.client.model.tail.BeeAbdomenModel;
import uk.kihira.tails.client.model.tail.BirdTailModel;
import uk.kihira.tails.client.model.tail.BunnyTailModel;
import uk.kihira.tails.client.model.tail.CatTailModel;
import uk.kihira.tails.client.model.tail.DevilTailModel;
import uk.kihira.tails.client.model.tail.DragonTailModel;
import uk.kihira.tails.client.model.tail.FluffyTailModel;
import uk.kihira.tails.client.model.tail.RaccoonTailModel;
import uk.kihira.tails.client.model.tail.SharkTailModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.part.PartRegistry;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.client.render.part.WingRenderer;

public final class PartRenderRegistry {

	private static final Map<ResourceLocation, PartRenderer> PART_RENDERER_REGISTRY = new HashMap<>();

	static {
		register(FLUFFY_TAIL, new PartRenderer(new FluffyTailModel()));
		register(DRAGON_TAIL, new PartRenderer(new DragonTailModel()));
		register(RACCOON_TAIL, new PartRenderer(new RaccoonTailModel()));
		register(DEVIL_TAIL, new PartRenderer(new DevilTailModel()));
		register(CAT_TAIL, new PartRenderer(new CatTailModel()));
		register(BIRD_TAIL, new PartRenderer(new BirdTailModel()));
		register(SHARK_TAIL, new PartRenderer(new SharkTailModel()));
		register(BUNNY_TAIL, new PartRenderer(new BunnyTailModel()));
		register(BEE_ABDOMEN, new PartRenderer(new BeeAbdomenModel()));

		register(FOX_EARS, new PartRenderer(new FoxEarsModel()));
		register(CAT_EARS, new PartRenderer(new CatEarsModel()));
		register(PANDA_EARS, new PartRenderer(new PandaEarsModel()));
		register(SMALL_CAT_EARS, new PartRenderer(new SmallCatEarsModel()));
		register(SEA_PICKLE, new PartRenderer(new SeaPickleModel()));
		register(ANTENNAE, new PartRenderer(new AntennaeModel()));
		register(DEMON_HORNS, new PartRenderer(new DemonHornsModel()));
		register(DEER_ANTLERS, new PartRenderer(new DeerAntlersModel()));
		register(AXOLOTL_GILLS, new PartRenderer(new AxolotlGillsModel()));
		register(STRIDER_WHISKERS, new PartRenderer(new StriderWhiskersModel()));

		register(BIG_WINGS, new WingRenderer());

		register(STANDARD_MUZZLE, new PartRenderer(new MuzzleModel(-2f, -3f, -9f, 4, 3, 5)));
		register(SLIM_MUZZLE, new PartRenderer(new MuzzleModel(-2f, -2f, -9f, 4, 2, 5)));
		register(THIN_MUZZLE, new PartRenderer(new MuzzleModel(-1.5f, -2f, -9f, 3, 2, 5, 0, 9)));
	}

	/**
	 * Adds the given part renderer to the registry.
	 * @param part The part to register the renderer for.
	 * @param renderer The part renderer to register.
	 */
	public static void register(ResourceLocation part, PartRenderer renderer) {
		if (part == null || renderer == null) throw new NullPointerException();
		PART_RENDERER_REGISTRY.put(part, renderer);
	}

	public static void register(PartRegistry.PartReference reference, PartRenderer renderer) {
		register(reference.id(), renderer);
	}

	public static PartRenderer getRenderer(Part part) {
		if (part == null) throw new NullPointerException();
		return PART_RENDERER_REGISTRY.get(part.getId());
	}
}