/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for devil tails.
 */
public class DevilTailModel extends PartModel {

	private final ModelRenderer tailBase;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail3;
	private final ModelRenderer tail4;
	private final ModelRenderer tail5;
	private final ModelRenderer tailTip;

	public DevilTailModel() {
		tailBase = new ModelRenderer(this, 0, 0);
		tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
		tailBase.setRotationPoint(0F, 0F, 0F);
		setRotationDegrees(tailBase, -30F, 0F, 0F);

		tail1 = new ModelRenderer(this, 0, 4);
		tail1.addBox(-0.5F, -0.5F, 0F, 1, 1, 4);
		tail1.setRotationPoint(0F, 0F, 1.8F);
		setRotationDegrees(tail1, -30F, 0F, 0F);

		tail2 = new ModelRenderer(this, 0, 9);
		tail2.addBox(-0.5F, -0.5F, 0F, 1, 1, 5);
		tail2.setRotationPoint(0F, 0F, 3.8F);
		setRotationDegrees(tail2, -30F, 0F, 0F);

		tail3 = new ModelRenderer(this, 0, 15);
		tail3.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
		tail3.setRotationPoint(0F, 0F, 4.8F);
		setRotationDegrees(tail3, 20F, 0F, 0F);

		tail4 = new ModelRenderer(this, 0, 19);
		tail4.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
		tail4.setRotationPoint(0F, 0F, 2.6F);
		setRotationDegrees(tail4, 50F, 0F, 0F);

		tail5 = new ModelRenderer(this, 0, 22);
		tail5.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
		tail5.setRotationPoint(0F, 0F, 1.7F);
		setRotationDegrees(tail5, 50F, 0F, 0F);

		tailTip = new ModelRenderer(this, 12, 0);
		tailTip.addBox(-2.5F, 0F, 0F, 5, 5, 0);
		tailTip.setRotationPoint(0F, 0F, 1.8F);
		setRotationDegrees(tailTip, 120F, 0F, 0F);

		tail5.addChild(tailTip);
		tail4.addChild(tail5);
		tail3.addChild(tail4);
		tail2.addChild(tail3);
		tail1.addChild(tail2);
		tailBase.addChild(tail1);
	}

	@Override
	public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {
		final float seed = getAnimationTime(6000, entity);
		final float xseed = getAnimationTime(12000, entity);
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getRidingEntity() == null) {
			if (entity instanceof PlayerEntity) {
				final double[] angles = getMotionAngles((PlayerEntity) entity, partialTicks);

				xAngleOffset = MathHelper.clamp(angles[0] / 3.5F, -1F, 0.275D);
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = Math.toRadians(13F);
			yAngleMultiplier = 0.25F;
		}

		setRotationRadians(tailBase, Math.toRadians(-30F) + xAngleOffset * 2F, MathHelper.cos(seed - 1) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail1, Math.toRadians(-30F) + xAngleOffset * 2F, MathHelper.cos(seed - 2) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail2, Math.toRadians(-30F) + xAngleOffset * 2F, MathHelper.cos(seed - 3) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail3, Math.toRadians(20F) - xAngleOffset * 2F + MathHelper.cos(xseed - 4) / 6F * yAngleMultiplier, MathHelper.cos(seed - 4) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 4) / 8F * yAngleMultiplier);
		setRotationRadians(tail4, Math.toRadians(50F) - xAngleOffset * 3F + MathHelper.cos(xseed - 5) / 8F * yAngleMultiplier, MathHelper.cos(seed - 5) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 5) / 8F * yAngleMultiplier);
		setRotationRadians(tail5, Math.toRadians(50F) - xAngleOffset * 4F + MathHelper.cos(xseed - 6) / 4F  * yAngleMultiplier, MathHelper.cos(seed - 6) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 6) / 8F * yAngleMultiplier);
		setRotationRadians(tailTip, Math.toRadians(120F) - xAngleOffset, 0F, 0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		if (subtype == 1)
			tailTip.showModel = false;

		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		tailTip.showModel = true;
	}
}
