/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import net.minecraft.client.renderer.LightTexture;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.common2.client.duck.TailsModelPart;

/**
 * <p>The blaze crown part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class BlazeCrownModel extends PartModel {

	@Override
	public void render(RenderContext ctx) {
		final TailsModelPart model = ctx.getModel();
		ctx.render(model);
		ctx.render(model.t$getChild("crown").t$getChild("rods"), LightTexture.FULL_BRIGHT, ctx.packedOverlay());
	}
}