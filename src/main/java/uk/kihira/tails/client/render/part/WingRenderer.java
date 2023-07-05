/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.part;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;

/**
 * A specialized {@link PartRenderer} for wings.
 */
public final class WingRenderer extends PartRenderer {

	public WingRenderer() {
		super(null);
	}

	@Override
	protected void doRender(RenderContext ctx) {
		final boolean isFlying = ctx.entity() instanceof Player player && player.getAbilities().flying && ctx.entity().hasImpulse || ctx.entity().fallDistance > 1.5F;
		final float timestep = PartModel.getAnimationTime(isFlying ? 500 : 6500, ctx.entity());
		final float angle = Mth.sin(timestep) * (isFlying ? 24F : 4F);
		final boolean small = ctx.info().getSubType().id().equals("small");
		final float scale = small ? 1F : 2F;

		ctx.poseStack().pushPose();

		ctx.poseStack().translate(0, -(scale * 8F) * PartModel.SCALE + (small ? 0.1F : 0), 0.1F);
		ctx.poseStack().mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
		ctx.poseStack().mulPose(new Quaternionf().rotateZ(Mth.HALF_PI));
		ctx.poseStack().scale(scale, scale, scale);
		ctx.poseStack().translate(0.1F, -0.4F * PartModel.SCALE, -0.025F);

		if (ctx.entity().isCrouching()) {
			ctx.poseStack().mulPose(new Quaternionf().rotateZ(35 * Mth.DEG_TO_RAD));
			ctx.poseStack().translate(0, -0.3, 0);
		}

		ctx.poseStack().pushPose();

		ctx.poseStack().translate(0F, 0F, 1F * PartModel.SCALE);
		ctx.poseStack().mulPose(new Quaternionf().rotateX((30F - angle) * Mth.DEG_TO_RAD));

		Matrix4f m = ctx.poseStack().last().pose();
		Matrix3f n = ctx.poseStack().last().normal();

		ctx.buffer().vertex(m, 0, 1, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(0, 0).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();
		ctx.buffer().vertex(m, 1, 1, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(1, 0).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();
		ctx.buffer().vertex(m, 1, 0, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(1, 1).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();
		ctx.buffer().vertex(m, 0, 0, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(0, 1).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();

		ctx.poseStack().popPose();

		ctx.poseStack().pushPose();

		// TODO: Why is this here? It causes one of the wings to be off-center.
		//ctx.poseStack().translate(0F, 0.3F * PartModel.SCALE, 0F);

		ctx.poseStack().mulPose(new Quaternionf().rotateX((-30F + angle) * Mth.DEG_TO_RAD));

		m = ctx.poseStack().last().pose();
		n = ctx.poseStack().last().normal();

		ctx.buffer().vertex(m, 0, 1, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(0, 0).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();
		ctx.buffer().vertex(m, 1, 1, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(1, 0).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();
		ctx.buffer().vertex(m, 1, 0, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(1, 1).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();
		ctx.buffer().vertex(m, 0, 0, 0).color(ctx.red(), ctx.green(), ctx.blue(), ctx.alpha()).uv(0, 1).overlayCoords(ctx.packedOverlay()).uv2(ctx.packedLight()).normal(n, 0, 0, 0).endVertex();

		ctx.poseStack().popPose();

		ctx.poseStack().popPose();
	}
}