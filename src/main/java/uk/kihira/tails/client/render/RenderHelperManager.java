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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.common.part.PartInfo;

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

	public static <T extends LivingEntity> void applyRenderHelpers(PoseStack poseStack, T entity, PartRenderer renderer, PartInfo info, MultiBufferSource bufferIn, VertexConsumer consumer, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		// TODO: Should we be doing this?
		final List<IRenderHelper<?>> helpers = entity instanceof Player ? getRenderHelpers(Player.class) : getRenderHelpers(entity.getClass());

		for (IRenderHelper helper : helpers)
			helper.onPreRenderTail(poseStack, entity, renderer, info, bufferIn, consumer, x, y, z, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}