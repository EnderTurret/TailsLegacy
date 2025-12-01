/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import static uk.kihira.tails.common2.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.Tails;

/**
 * Handles registration of tail {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 * @see RegisterPartRenderersEvent
 */
@Internal
@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
public final class TailRegistrationHandler {

	private TailRegistrationHandler() {}

	@SubscribeEvent
	static void registerPartRenderers(RegisterPartRenderersEvent e) {
		e.register(FLUFFY_TAIL, new FluffyTailModel());
		e.register(DRAGON_TAIL, new DragonTailModel());
		e.register(RACCOON_TAIL, new RaccoonTailModel());
		e.register(DEVIL_TAIL, new DevilTailModel());
		e.register(CAT_TAIL, new CatTailModel());
		e.register(BIRD_TAIL, new BirdTailModel());
		e.register(SHARK_TAIL, new SharkTailModel());
		e.register(BEE_ABDOMEN, new BeeAbdomenModel());
		e.register(SCORPION_TAIL, new ScorpionTailModel());
		e.register(THICK_TAIL, new ThickTailModel());
	}
}