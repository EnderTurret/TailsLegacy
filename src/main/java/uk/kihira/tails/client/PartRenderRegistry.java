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
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
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
import uk.kihira.tails.client.model.tail.ScorpionTailModel;
import uk.kihira.tails.client.model.tail.SharkTailModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.client.render.part.WingRenderer;
import uk.kihira.tails.common.Tails;

@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PartRenderRegistry {

	private static final Map<ResourceLocation, PartRenderer> PART_RENDERER_REGISTRY = new HashMap<>();

	@SubscribeEvent
	static void registerPartRenderers(RegisterPartRenderersEvent e) {
		e.register(FLUFFY_TAIL, new PartRenderer(new FluffyTailModel()));
		e.register(DRAGON_TAIL, new PartRenderer(new DragonTailModel()));
		e.register(RACCOON_TAIL, new PartRenderer(new RaccoonTailModel()));
		e.register(DEVIL_TAIL, new PartRenderer(new DevilTailModel()));
		e.register(CAT_TAIL, new PartRenderer(new CatTailModel()));
		e.register(BIRD_TAIL, new PartRenderer(new BirdTailModel()));
		e.register(SHARK_TAIL, new PartRenderer(new SharkTailModel()));
		e.register(BUNNY_TAIL, new PartRenderer(new BunnyTailModel()));
		e.register(BEE_ABDOMEN, new PartRenderer(new BeeAbdomenModel()));
		e.register(SCORPION_TAIL, new PartRenderer(new ScorpionTailModel()));

		e.register(FOX_EARS, new PartRenderer(new FoxEarsModel()));
		e.register(CAT_EARS, new PartRenderer(new CatEarsModel()));
		e.register(PANDA_EARS, new PartRenderer(new PandaEarsModel()));
		e.register(SMALL_CAT_EARS, new PartRenderer(new SmallCatEarsModel()));
		e.register(SEA_PICKLE, new PartRenderer(new SeaPickleModel()));
		e.register(ANTENNAE, new PartRenderer(new AntennaeModel()));
		e.register(DEMON_HORNS, new PartRenderer(new DemonHornsModel()));
		e.register(DEER_ANTLERS, new PartRenderer(new DeerAntlersModel()));
		e.register(AXOLOTL_GILLS, new PartRenderer(new AxolotlGillsModel()));
		e.register(STRIDER_WHISKERS, new PartRenderer(new StriderWhiskersModel()));

		e.register(BIG_WINGS, new WingRenderer());

		e.register(STANDARD_MUZZLE, new PartRenderer(new MuzzleModel(-2f, -3f, -9f, 4, 3, 5)));
		e.register(SLIM_MUZZLE, new PartRenderer(new MuzzleModel(-2f, -2f, -9f, 4, 2, 5)));
		e.register(THIN_MUZZLE, new PartRenderer(new MuzzleModel(-1.5f, -2f, -9f, 3, 2, 5, 0, 9)));
	}

	@Internal
	public static void reload() {
		PART_RENDERER_REGISTRY.clear();

		final Map<ResourceLocation, PartRenderer> map = new ConcurrentHashMap<>();
		ModLoader.get().postEvent(new RegisterPartRenderersEvent(map));

		PART_RENDERER_REGISTRY.putAll(map);
	}

	public static PartRenderer getRenderer(Part part) {
		if (part == null) throw new NullPointerException();
		return PART_RENDERER_REGISTRY.get(part.getId());
	}
}