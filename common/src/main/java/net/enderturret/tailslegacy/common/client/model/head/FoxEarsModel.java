/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model.head;

import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.PartModel;
import net.enderturret.tailslegacy.common.client.render.RenderContext;

/**
 * <p>The fox ears part model.</p>
 * <p>Model created by Adeon.</p>
 */
final class FoxEarsModel extends PartModel {

	@Override
	public void render(RenderContext ctx) {
		final TailsModelPart model = ctx.getModel();
		final TailsModelPart leftEar = model.t$getChild("leftEar");
		final TailsModelPart rightEar = model.t$getChild("rightEar");

		ctx.poseStack().t$push();

		ctx.poseStack().t$translate(0f, 0f, -0.0625f);

		if ("inward".equals(ctx.info().getSubType().id()))
			ctx.poseStack().t$translate(-0.4375f, 0f, 0f);

		ctx.render(leftEar);

		if ("inward".equals(ctx.info().getSubType().id()))
			ctx.poseStack().t$translate(0.875f, 0f, 0f);

		ctx.render(rightEar);

		ctx.poseStack().t$pop();
	}
}