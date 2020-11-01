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

public class DragonTailModel extends PartModel {

	private final ModelRenderer tailBase;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail3;

	private final ModelRenderer tailSubBase;
	private final ModelRenderer tailSub1;
	private final ModelRenderer tailSub2;
	private final ModelRenderer tailSub3;

	public DragonTailModel() {
		tailBase = new ModelRenderer(this, 22, 0);
		tailBase.addBox(-2.5F, -2.5F, -2F, 5, 5, 8);
		setRotationDegrees(tailBase, -40F, 0F, 0F);

		tail1 = new ModelRenderer(this, 0, 0);
		tail1.addBox(-2F, -2F, 0F, 4, 4, 7);
		tail1.setRotationPoint(0F, 0.3F, 5F);
		setRotationDegrees(tail1, -8F, 0F, 0F);

		tail2 = new ModelRenderer(this, 0, 11);
		tail2.addBox(-1.5F, -1.5F, 0F, 3, 3, 8);
		tail2.setRotationPoint(0F, 0.2F, 5.5F);
		setRotationDegrees(tail2, 10F, 0F, 0F);

		tail3 = new ModelRenderer(this, 0, 22);
		tail3.addBox(-1F, -1F, 0F, 2, 2, 7);
		tail3.setRotationPoint(0F, 0.4F, 7.5F);
		setRotationDegrees(tail3, 20F, 0F, 0F);

		tail2.addChild(tail3);
		tail1.addChild(tail2);
		tailBase.addChild(tail1);

		tailSubBase = new ModelRenderer(this, 22, 5);
		tailSubBase.addBox(0F, -7.25F, -2F, 0, 5, 8);
		setRotationDegrees(tailSubBase, -40F, 0F, 0F);

		tailSub1 = new ModelRenderer(this, 22, 11);
		tailSub1.addBox(0F, -6.75F, 1F, 0, 5, 7);
		tailSub1.setRotationPoint(0F, 0.3F, 5F);
		setRotationDegrees(tailSub1, -8F, 0F, 0F);

		tailSub2 = new ModelRenderer(this, 22, 15);
		tailSub2.addBox(0F, -6.25F, 1F, 0, 5, 8);
		tailSub2.setRotationPoint(0F, 0.2F, 5.5F);
		setRotationDegrees(tailSub2, 10F, 0F, 0F);

		tailSub3 = new ModelRenderer(this, 29, 6);
		tailSub3.addBox(0F, -5.75F, 1F, 0, 5, 7);
		tailSub3.setRotationPoint(0F, 0.4F, 7.5F);
		setRotationDegrees(tailSub3, 20F, 0F, 0F);

		tailSub2.addChild(tailSub3);
		tailSub1.addChild(tailSub2);
		tailSubBase.addChild(tailSub1);
	}

	@Override
	public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getRidingEntity() == null) {
			if (entity instanceof PlayerEntity) {
				final double[] angles = getMotionAngles((PlayerEntity) entity, partialTicks);

				xAngleOffset = MathHelper.clamp(angles[0] / 5F, -1D, 0.45D);
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = Math.toRadians(12F);
			yAngleMultiplier = 0.25F;
		}

		final float timestep = getAnimationTime(4000D, entity);
		setRotationRadians(tailBase, Math.toRadians(-40F) + xAngleOffset * 2F, (float) Math.cos(timestep - 1) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail1, Math.toRadians(-8F) + xAngleOffset * 2F, (float) Math.cos(timestep - 2) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail2, Math.toRadians(10F) - xAngleOffset / 4F, (float) Math.cos(timestep - 3) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail3, Math.toRadians(20F) - xAngleOffset, (float) Math.cos(timestep - 4) / 5F * yAngleMultiplier, 0F);

		if (subtype == 1) {
			setRotationRadians(tailSubBase, Math.toRadians(-40F) + xAngleOffset * 2F, (float) Math.cos(timestep - 1) / 5F * yAngleMultiplier, 0F);
			setRotationRadians(tailSub1, Math.toRadians(-8F) + xAngleOffset * 2F, (float) Math.cos(timestep - 2) / 5F * yAngleMultiplier, 0F);
			setRotationRadians(tailSub2, Math.toRadians(10F) - xAngleOffset / 4F, (float) Math.cos(timestep - 3) / 5F * yAngleMultiplier, 0F);
			setRotationRadians(tailSub3, Math.toRadians(20F) - xAngleOffset, (float) Math.cos(timestep - 4) / 5F * yAngleMultiplier, 0F);
		}
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (subtype == 1)
			tailSubBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
