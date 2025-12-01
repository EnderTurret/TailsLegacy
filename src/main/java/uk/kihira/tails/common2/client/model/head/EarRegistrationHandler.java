/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.model.head;

import static uk.kihira.tails.common2.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common2.client.api.PartRendererRegistrar;
import uk.kihira.tails.common2.client.render.part.PartRenderer;

/**
 * Handles registration of ear {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 */
@Internal
public final class EarRegistrationHandler {

	private EarRegistrationHandler() {}

	public static void registerPartRenderers(PartRendererRegistrar registrar) {
		registrar.register(FOX_EARS, new FoxEarsModel());
		registrar.register(BLAZE_CROWN, new BlazeCrownModel());
	}
}