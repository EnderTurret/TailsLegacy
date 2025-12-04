/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.body;

import static uk.kihira.tails.common.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.render.part.PartRenderer;

/**
 * Handles registration of tail {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 */
@Internal
public final class TailRegistrationHandler {

	private TailRegistrationHandler() {}

	public static void registerPartRenderers(PartRendererRegistrar registrar) {
		registrar.register(FLUFFY_TAIL, new FluffyTailModel());
		registrar.register(DRAGON_TAIL, new DragonTailModel());
		registrar.register(RACCOON_TAIL, new RaccoonTailModel());
		registrar.register(DEVIL_TAIL, new DevilTailModel());
		registrar.register(CAT_TAIL, new CatTailModel());
		registrar.register(BIRD_TAIL, new BirdTailModel());
		registrar.register(SHARK_TAIL, new SharkTailModel());
		registrar.register(SCORPION_TAIL, new ScorpionTailModel());
		registrar.register(THICK_TAIL, new ThickTailModel());
	}
}