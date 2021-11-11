/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;

/**
 * A specialized {@link PartRenderer} for wings.
 */
public class WingRenderer extends PartRenderer {

	public WingRenderer() {
		super(null);
	}

	@Override
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder renderer, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		final boolean isFlying = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isFlying && entity.isAirBorne || entity.fallDistance > 1.5F;
		final float timestep = PartModel.getAnimationTime(isFlying ? 500 : 6500, entity);
		final float angle = MathHelper.sin(timestep) * (isFlying ? 24F : 4F);
		final float scale = info.getSubType() == 1 ? 1F : 2F;

		matrixStack.push();

		matrixStack.translate(0, -(scale * 8F) * PartModel.SCALE + (info.getSubType() == 1 ? 0.1F : 0), 0.1F);
		matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(0.1F, -0.4F * PartModel.SCALE, -0.025F);

		matrixStack.push();

		matrixStack.translate(0F, 0F, 1F * PartModel.SCALE);
		matrixStack.rotate(Vector3f.XP.rotationDegrees(30F - angle));
		Matrix4f m = matrixStack.getLast().getMatrix();
		Matrix3f n = matrixStack.getLast().getNormal();

		renderer.pos(m, 0, 1, 0).color(red, green, blue, alpha).tex(0, 0).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.pos(m, 1, 1, 0).color(red, green, blue, alpha).tex(1, 0).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.pos(m, 1, 0, 0).color(red, green, blue, alpha).tex(1, 1).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.pos(m, 0, 0, 0).color(red, green, blue, alpha).tex(0, 1).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();

		matrixStack.pop();

		matrixStack.push();

		matrixStack.translate(0F, 0.3F * PartModel.SCALE, 0F);
		matrixStack.rotate(Vector3f.XP.rotationDegrees(-30F + angle));
		m = matrixStack.getLast().getMatrix();
		n = matrixStack.getLast().getNormal();
		renderer.pos(m, 0, 1, 0).color(red, green, blue, alpha).tex(0, 0).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.pos(m, 1, 1, 0).color(red, green, blue, alpha).tex(1, 0).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.pos(m, 1, 0, 0).color(red, green, blue, alpha).tex(1, 1).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();
		renderer.pos(m, 0, 0, 0).color(red, green, blue, alpha).tex(0, 1).overlay(packedOverlayIn).lightmap(packedLightIn).normal(n, 0, 0, 0).endVertex();

		matrixStack.pop();

		matrixStack.pop();
	}
}