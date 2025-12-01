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
import java.util.List;
import java.util.Objects;

import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * The {@link IRenderHelper} manager.
 * This manages and caches {@link IRenderHelper IRenderHelpers} for various entity classes.
 * @author EnderTurret
 */
public final class RenderHelperManager {

	private static final List<IRenderHelper> RENDER_HELPERS = new ArrayList<>();
	private static final List<IRenderHelper> RENDER_HELPERS_VIEW = Collections.unmodifiableList(RENDER_HELPERS);

	public static <T extends LivingEntity> void registerRenderHelper(IRenderHelper helper) {
		RENDER_HELPERS.add(Objects.requireNonNull(helper));
	}

	public static List<IRenderHelper> getRenderHelpers() {
		return RENDER_HELPERS_VIEW;
	}

	public static <T extends LivingEntity> void applyRenderHelpers(RenderContext ctx, PartRenderer renderer) {
		for (IRenderHelper helper : getRenderHelpers())
			helper.onPreRenderTail(ctx, renderer);
	}
}