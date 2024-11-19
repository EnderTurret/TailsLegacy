/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import net.minecraft.client.model.geom.ModelPart;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;

/**
 * <p>The fox ears part model.</p>
 * <p>Model created by Adeon.</p>
 */
final class FoxEarsModel extends PartModel {

	@Override
	public void render(RenderContext ctx) {
		final ModelPart model = ctx.getModel();
		final ModelPart leftEar = model.getChild("leftEar");
		final ModelPart rightEar = model.getChild("rightEar");

		ctx.poseStack().pushPose();

		ctx.poseStack().translate(0f, 0f, -0.0625f);

		if ("inward".equals(ctx.info().getSubType().id()))
			ctx.poseStack().translate(-0.4375f, 0f, 0f);

		ctx.render(leftEar);

		if ("inward".equals(ctx.info().getSubType().id()))
			ctx.poseStack().translate(0.875f, 0f, 0f);

		ctx.render(rightEar);

		ctx.poseStack().popPose();
	}
}