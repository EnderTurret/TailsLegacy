/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.head;

import static uk.kihira.tails.common.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.render.part.PartRenderer;

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