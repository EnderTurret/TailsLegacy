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

public class RaccoonTailModel extends PartModel {

	private final ModelRenderer tailBase;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;

	public RaccoonTailModel() {
		tailBase = new ModelRenderer(this, 12, 16);
		tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
		tailBase.setRotationPoint(0F, 0F, 0F);

		tail1 = new ModelRenderer(this, 0, 16);
		tail1.addBox(-1.5F, -1.5F, 0F, 3, 3, 3);
		tail1.setRotationPoint(0F, 0F, 1F);
		setRotationDegrees(tail1, -40F, 0F, 0F);

		tail2 = new ModelRenderer(this, 0, 0);
		tail2.addBox(-2F, -2F, 0F, 4, 4, 12);
		tail2.setRotationPoint(0F, 0F, 2F);
		setRotationDegrees(tail2, -30F, 0F, 0F);

		final ModelRenderer tailTip = new ModelRenderer(this, 0, 22);
		tailTip.addBox(-1.5F, -1.5F, 0F, 3, 3, 1);
		tailTip.setRotationPoint(0F, 0F, 12F);

		tail2.addChild(tailTip);
		tail1.addChild(tail2);
		tailBase.addChild(tail1);
	}

	@Override
	public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.

		if (entity.getRidingEntity() == null) {
			if (entity instanceof PlayerEntity) {
				final double[] angles = getMotionAngles((PlayerEntity) entity, partialTicks);

				xAngleOffset = angles[0];
				yAngleOffset = angles[1];
				zAngleOffset = angles[2];
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.

				xAngleOffset = MathHelper.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
				zAngleOffset = MathHelper.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
			}
		}
		// Mounted
		else {
			xAngleOffset = Math.toRadians(20F);
			yAngleMultiplier = 0.2F;
		}

		setRotationRadians(tailBase, xAngleOffset, (-zAngleOffset + MathHelper.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
		setRotationRadians(tail1, Math.toRadians(-40F) + xAngleOffset, (-zAngleOffset + MathHelper.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
		setRotationRadians(tail2, Math.toRadians(-30F) + xAngleOffset, (-zAngleOffset + MathHelper.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}