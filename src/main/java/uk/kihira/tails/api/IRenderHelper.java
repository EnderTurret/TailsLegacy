/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * A pre-render callback for part rendering.<br>
 * Called just before rendering in {@link PartRenderer#preRender(RenderContext)}.
 *
 * @param <T> The type of entity this helper is for.
 */
@FunctionalInterface
public interface IRenderHelper<T extends LivingEntity> {

	/**
	 * Handles pre-render transformations and other fun stuff.<br><br>
	 * You could render a sea pickle above the player's head here, if you wanted to.
	 * @param ctx The render context.
	 * @param renderer The part renderer.
	 */
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer);
}