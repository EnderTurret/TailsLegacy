/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.head;

import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.PartModel;
import uk.kihira.tails.common.client.render.RenderContext;

/**
 * <p>The blaze crown part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class BlazeCrownModel extends PartModel {

	private static final int FULL_BRIGHT = 15728880;

	@Override
	public void render(RenderContext ctx) {
		final TailsModelPart model = ctx.getModel();
		ctx.render(model);
		ctx.render(model.t$getChild("crown").t$getChild("rods"), FULL_BRIGHT, ctx.packedOverlay());
	}
}