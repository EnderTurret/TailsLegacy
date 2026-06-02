/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model.body;

import static net.enderturret.tailslegacy.common.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.client.api.PartRendererRegistrar;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;

/**
 * Handles registration of tail {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 */
@Internal
public final class TailRegistrationHandler {

	private TailRegistrationHandler() {}

	public static void registerPartRenderers(PartRendererRegistrar registrar) {
		registrar.register(FLUFFY_TAIL, new FluffyTailModel());
	}
}