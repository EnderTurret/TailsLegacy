/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.entity.LivingEntity;
import uk.kihira.tails.api.IRenderHelper;

/**
 * The {@link IRenderHelper} manager.<br>
 * Manages and caches {@link IRenderHelper IRenderHelpers} for various entity classes.
 * @author EnderTurret
 */
public class RenderHelperManager {

	private static final Map<Class<? extends LivingEntity>, List<IRenderHelper<?>>> RENDER_HELPERS = new HashMap<>();

	private static boolean dirty = false;
	private static final Map<Class<? extends LivingEntity>, List<IRenderHelper<?>>> RENDER_HELPER_CACHE = new HashMap<>();

	public static <T extends LivingEntity> void registerRenderHelper(Class<T> clazz, IRenderHelper<T> helper) {
		if (helper != null && clazz != null) {
			RENDER_HELPERS.computeIfAbsent(clazz, k -> new ArrayList<>(1)).add(helper);
			dirty = true;
		} else
			throw new IllegalArgumentException("Attempted to register an invalid IRenderHelper (" + helper + ") for class " + (clazz == null ? "null" : clazz.getName()) + "!");
	}

	public static <T extends LivingEntity> List<IRenderHelper<?>> getRenderHelpers(Class<T> clazz) {
		if (dirty) {
			RENDER_HELPER_CACHE.clear();
			dirty = false;
		}

		return RENDER_HELPER_CACHE.computeIfAbsent(clazz, RenderHelperManager::buildCache);
	}

	private static List<IRenderHelper<?>> buildCache(Class<? extends LivingEntity> clazz) {
		final List<IRenderHelper<?>> helpers = new ArrayList<>();

		Class<?> parent = clazz;

		do {
			helpers.addAll(RENDER_HELPERS.getOrDefault(parent, Collections.emptyList()));
			parent = parent.getSuperclass();
		} while (parent != LivingEntity.class);

		return helpers;
	}
}