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
	protected void doRender(PoseStack matrixStack, LivingEntity entity, PartInfo info, VertexConsumer renderer, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		final boolean isFlying = entity instanceof Player && ((Player) entity).getAbilities().flying && entity.hasImpulse || entity.fallDistance > 1.5F;
		final float timestep = PartModel.getAnimationTime(isFlying ? 500 : 6500, entity);
		final float angle = Mth.sin(timestep) * (isFlying ? 24F : 4F);
		final float scale = info.getSubType() == 1 ? 1F : 2F;

		matrixStack.pushPose();

		matrixStack.translate(0, -(scale * 8F) * PartModel.SCALE + (info.getSubType() == 1 ? 0.1F : 0), 0.1F);
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(0.1F, -0.4F * PartModel.SCALE, -0.025F);

		matrixStack.pushPose();

		matrixStack.translate(0F, 0F, 1F * PartModel.SCALE);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(30F - angle));
		Matrix4f m = matrixStack.last().pose();
		Matrix3f n = matrixStack.last().normal();

		renderer.vertex(m, 0, 1, 0).color(red, green, blue, alpha).uv(0, 0).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.vertex(m, 1, 1, 0).color(red, green, blue, alpha).uv(1, 0).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.vertex(m, 1, 0, 0).color(red, green, blue, alpha).uv(1, 1).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.vertex(m, 0, 0, 0).color(red, green, blue, alpha).uv(0, 1).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();

		matrixStack.popPose();

		matrixStack.pushPose();

		matrixStack.translate(0F, 0.3F * PartModel.SCALE, 0F);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-30F + angle));
		m = matrixStack.last().pose();
		n = matrixStack.last().normal();
		renderer.vertex(m, 0, 1, 0).color(red, green, blue, alpha).uv(0, 0).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.vertex(m, 1, 1, 0).color(red, green, blue, alpha).uv(1, 0).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.vertex(m, 1, 0, 0).color(red, green, blue, alpha).uv(1, 1).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.vertex(m, 0, 0, 0).color(red, green, blue, alpha).uv(0, 1).overlayCoords(packedOverlayIn).uv2(packedLightIn).normal(n, 0, 0, 0).endVertex();

		matrixStack.popPose();

		matrixStack.popPose();
	}
}