/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common.part.PartInfo;

/**
 * A specialized {@link PartRenderer} for wings.
 */
public class WingRenderer extends PartRenderer {

	public WingRenderer() {
		super(null);
	}

	@Override
	protected void doRender(PoseStack poseStack, LivingEntity entity, PartInfo info, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		final boolean isFlying = entity instanceof Player player && player.getAbilities().flying && entity.hasImpulse || entity.fallDistance > 1.5F;
		final float timestep = PartModel.getAnimationTime(isFlying ? 500 : 6500, entity);
		final float angle = Mth.sin(timestep) * (isFlying ? 24F : 4F);
		final float scale = info.getSubType() == 1 ? 1F : 2F;

		poseStack.pushPose();

		poseStack.translate(0, -(scale * 8F) * PartModel.SCALE + (info.getSubType() == 1 ? 0.1F : 0), 0.1F);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(90));
		poseStack.scale(scale, scale, scale);
		poseStack.translate(0.1F, -0.4F * PartModel.SCALE, -0.025F);

		poseStack.pushPose();

		poseStack.translate(0F, 0F, 1F * PartModel.SCALE);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(30F - angle));
		Matrix4f m = poseStack.last().pose();
		Matrix3f n = poseStack.last().normal();

		buffer.vertex(m, 0, 1, 0).color(red, green, blue, alpha).uv(0, 0).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();
		buffer.vertex(m, 1, 1, 0).color(red, green, blue, alpha).uv(1, 0).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();
		buffer.vertex(m, 1, 0, 0).color(red, green, blue, alpha).uv(1, 1).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();
		buffer.vertex(m, 0, 0, 0).color(red, green, blue, alpha).uv(0, 1).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();

		poseStack.popPose();

		poseStack.pushPose();

		poseStack.translate(0F, 0.3F * PartModel.SCALE, 0F);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(-30F + angle));
		m = poseStack.last().pose();
		n = poseStack.last().normal();
		buffer.vertex(m, 0, 1, 0).color(red, green, blue, alpha).uv(0, 0).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();
		buffer.vertex(m, 1, 1, 0).color(red, green, blue, alpha).uv(1, 0).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();
		buffer.vertex(m, 1, 0, 0).color(red, green, blue, alpha).uv(1, 1).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();
		buffer.vertex(m, 0, 0, 0).color(red, green, blue, alpha).uv(0, 1).overlayCoords(packedOverlay).uv2(packedLight).normal(n, 0, 0, 0).endVertex();

		poseStack.popPose();

		poseStack.popPose();
	}
}