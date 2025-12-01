/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import static uk.kihira.tails.common2.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.Tails;

/**
 * Handles registration of ear {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 * @see RegisterPartRenderersEvent
 */
@Internal
@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
public final class EarRegistrationHandler {

	private EarRegistrationHandler() {}

	@SubscribeEvent
	static void registerPartRenderers(RegisterPartRenderersEvent e) {
		e.register(FOX_EARS, new FoxEarsModel());
		e.register(BLAZE_CROWN, new BlazeCrownModel());
	}
}