/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.render.part;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.model.PartModel;
import uk.kihira.tails.common.client.model.PartModelHelper;
import uk.kihira.tails.common.client.render.RenderContext;

/**
 * A specialized {@link PartRenderer} for wings.
 */
public final class WingRenderer extends PartRenderer {

	private static final float SCALE = 0.0625F;

	public WingRenderer() {
		super(null);
	}

	@Override
	protected void doRender(RenderContext ctx) {
		final boolean isFlying = ctx.entity().t$isFlying();
		final float timestep = PartModelHelper.getAnimationTime(isFlying ? 500 : 6500, ctx.entity());
		final float angle = TailsMath.sin(timestep) * (isFlying ? 24F : 4F);
		final boolean small = "small".equals(ctx.info().getSubType().id());
		final float scale = small ? 1F : 2F;

		final TailsPoseStack pose = ctx.poseStack();

		pose.t$push();

		pose.t$translate(0, -(scale * 8F) * SCALE + (small ? 0.1F : 0), 0.1F);
		pose.t$rotateY(TailsMath.HALF_PI);
		pose.t$rotateZ(TailsMath.HALF_PI);
		pose.t$scale(scale, scale, scale);
		pose.t$translate(0.1F, -0.4F * SCALE, -0.025F);

		if (ctx.entity().t$isCrouching()) {
			pose.t$rotateZ(35 * TailsMath.DEG_TO_RAD);
			pose.t$translate(0, -0.3, 0);
		}

		pose.t$push();

		pose.t$translate(0F, 0F, 1F * SCALE);
		pose.t$rotateX((30F - angle) * TailsMath.DEG_TO_RAD);

		ctx.buffer().t$submitCustomGeometry(pose, (p, consumer) -> {
			consumer.t$beginVertex(p, 0, 1, 0).t$color(ctx.color()).t$uv(0, 0).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
			consumer.t$beginVertex(p, 1, 1, 0).t$color(ctx.color()).t$uv(1, 0).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
			consumer.t$beginVertex(p, 1, 0, 0).t$color(ctx.color()).t$uv(1, 1).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
			consumer.t$beginVertex(p, 0, 0, 0).t$color(ctx.color()).t$uv(0, 1).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
		});

		pose.t$pop();

		pose.t$push();

		// TODO: Why is this here? It causes one of the wings to be off-center.
		//pose.translate(0F, 0.3F * PartModel.SCALE, 0F);

		pose.t$rotateX((-30F + angle) * TailsMath.DEG_TO_RAD);

		ctx.buffer().t$submitCustomGeometry(pose, (p, consumer) -> {
			consumer.t$beginVertex(p, 0, 1, 0).t$color(ctx.color()).t$uv(0, 0).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
			consumer.t$beginVertex(p, 1, 1, 0).t$color(ctx.color()).t$uv(1, 0).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
			consumer.t$beginVertex(p, 1, 0, 0).t$color(ctx.color()).t$uv(1, 1).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
			consumer.t$beginVertex(p, 0, 0, 0).t$color(ctx.color()).t$uv(0, 1).t$overlay(ctx.packedOverlay()).t$light(ctx.packedLight()).t$normal(p, 0, 0, 0).t$endVertex();
		});

		pose.t$pop();

		pose.t$pop();
	}
}