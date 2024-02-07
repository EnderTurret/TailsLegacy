/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * The {@link IRenderHelper} manager.
 * This manages and caches {@link IRenderHelper IRenderHelpers} for various entity classes.
 * @author EnderTurret
 */
public final class RenderHelperManager {

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

	public static <T extends LivingEntity> void applyRenderHelpers(RenderContext ctx, PartRenderer renderer) {
		// TODO: Should we be doing this?
		final List<IRenderHelper<?>> helpers = ctx.entity() instanceof Player ? getRenderHelpers(Player.class) : getRenderHelpers(ctx.entity().getClass());

		for (IRenderHelper helper : helpers)
			helper.onPreRenderTail(ctx, renderer);
	}
}