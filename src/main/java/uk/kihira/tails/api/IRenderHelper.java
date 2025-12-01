/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * A pre-render callback for part rendering.
 * Called just before rendering in {@link PartRenderer#preRender(RenderContext)}.
 */
@FunctionalInterface
public interface IRenderHelper {

	/**
	 * <p>Handles pre-render transformations and other fun stuff.</p>
	 * <p>You could render a sea pickle above the player's head here, if you wanted to.</p>
	 * @param ctx The render context.
	 * @param renderer The part renderer.
	 */
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer);
}