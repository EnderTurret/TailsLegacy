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
 * The model for cat tails.<br>
 * (Not to be confused with cattails.)
 */
public class CatTailModel extends PartModel {

	private final ModelRenderer tailBase;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail3;
	private final ModelRenderer tail4;
	private final ModelRenderer tail5;

	public CatTailModel() {
		textureWidth = 64;
		textureHeight = 32;

		tailBase = new ModelRenderer(this, 0, 0);
		tailBase.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
		tailBase.setRotationPoint(0F, 0F, 0F);
		setRotationDegrees(tailBase, 0F, 0F, 0F);

		tail1 = new ModelRenderer(this, 0, 3);
		tail1.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
		tail1.setRotationPoint(0F, 0F, 1.75F);
		setRotationDegrees(tail1, 0F, 0F, 0F);

		tail2 = new ModelRenderer(this, 0, 7);
		tail2.addBox(-0.5F, -0.5F, 0F, 1, 1, 6);
		tail2.setRotationPoint(0F, 0F, 2.75F);
		setRotationDegrees(tail2, 0F, 0F, 0F);

		tail3 = new ModelRenderer(this, 0, 14);
		tail3.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
		tail3.setRotationPoint(0F, 0F, 5.75F);
		setRotationDegrees(tail3, 0F, 0F, 0F);

		tail4 = new ModelRenderer(this, 0, 18);
		tail4.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
		tail4.setRotationPoint(0F, 0F, 2.75F);
		setRotationDegrees(tail4, 0F, 0F, 0F);

		tail5 = new ModelRenderer(this, 0, 21);
		tail5.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
		tail5.setRotationPoint(0F, 0F, 1.75F);
		setRotationDegrees(tail5, 0F, 0F, 0F);

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

				xAngleOffset = MathHelper.clamp(angles[0] / 3.5F, -1F, 0.33D);
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.
			}
		}
		else {
			xAngleOffset = Math.toRadians(13F);
			yAngleMultiplier = 0.25F;
		}

		setRotationRadians(tailBase, Math.toRadians(-30F) + xAngleOffset * 2F, MathHelper.cos(seed - 1) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail1, Math.toRadians(-30F) + xAngleOffset * 2F, MathHelper.cos(seed - 2) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail2, Math.toRadians(-30F) + xAngleOffset * 2F, MathHelper.cos(seed - 3) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 3) / 16F);
		setRotationRadians(tail3, Math.toRadians(20F) - xAngleOffset * 2F + MathHelper.cos(xseed - 4) / 8F, MathHelper.cos(seed - 4) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 4) / 8F);
		setRotationRadians(tail4, Math.toRadians(50F) - xAngleOffset * 2.5F + MathHelper.cos(xseed - 5) / 10F, MathHelper.cos(seed - 5) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 5) / 8F);
		setRotationRadians(tail5, Math.toRadians(50F) - xAngleOffset * 3F + MathHelper.cos(xseed - 6) / 10F, MathHelper.cos(seed - 6) / 8F * yAngleMultiplier, MathHelper.cos(xseed - 6) / 8F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
