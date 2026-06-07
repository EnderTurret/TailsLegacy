/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model.head;

import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.PartModel;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.render.RenderContext;

/**
 * The sea pickle part model.
 */
final class SeaPickleModel extends PartModel {

	@Override
	public void render0(RenderContext ctx, Part part) {
		final TailsModelPart model = ctx.getModel();
		final TailsModelPart top = model.t$getChild("top");

		top.t$setVisible(ctx.entity().t$inLiquid() || ctx.entity().t$isPreview());

		super.render0(ctx, part);
	}
}