/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.render;

import static net.enderturret.tailslegacy.common.client.part.PartRegistry.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.api.PartRendererRegistrar;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.model.DefaultPartModel;
import net.enderturret.tailslegacy.common.client.model.MuzzleModel;
import net.enderturret.tailslegacy.common.client.model.body.TailRegistrationHandler;
import net.enderturret.tailslegacy.common.client.model.head.EarRegistrationHandler;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;
import net.enderturret.tailslegacy.common.client.render.part.WingRenderer;

/**
 * Manages the registry of {@link PartRenderer PartRenderers} that correspond to different parts.
 * @author EnderTurret
 */
public final class PartRenderRegistry {

	private static final Map<TResourceLocation, PartRenderer> PART_RENDERER_REGISTRY = new HashMap<>();

	private static void registerPartRenderers(PartRendererRegistrar registrar) {
		registrar.register(BIG_WINGS, new WingRenderer());

		registrar.register(STANDARD_MUZZLE, new MuzzleModel(-2f, -3f, -9f, 4, 3, 5));
		registrar.register(SLIM_MUZZLE, new MuzzleModel(-2f, -2f, -9f, 4, 2, 5));
		registrar.register(THIN_MUZZLE, new MuzzleModel(-1.5f, -2f, -9f, 3, 2, 5, 0, 9));

		TailRegistrationHandler.registerPartRenderers(registrar);
		EarRegistrationHandler.registerPartRenderers(registrar);

		TailsClientPlatform.get().fireRegisterPartRenderersEvent(registrar);
	}

	/**
	 * Reloads the entire part renderer registry.
	 * An event will be fired to rebuild the part renderer registry.
	 */
	@Internal
	public static void reload() {
		PART_RENDERER_REGISTRY.clear();

		final Map<TResourceLocation, PartRenderer> map = new ConcurrentHashMap<>();
		final PartRendererRegistrar registrar = new PartRendererRegistrar(map);

		registerPartRenderers(registrar);

		PART_RENDERER_REGISTRY.putAll(map);
	}

	/**
	 * Returns the {@link PartRenderer} associated with the given part, or {@code null} if no such renderer exists.
	 * @param part The {@link Part} to retrieve the {@link PartRenderer} for.
	 * @return The {@link PartRenderer}.
	 */
	public static PartRenderer getRenderer(Part part) {
		if (part == null) throw new NullPointerException();
		return PART_RENDERER_REGISTRY.computeIfAbsent(part.getId(), k -> new PartRenderer(DefaultPartModel.INSTANCE));
	}
}