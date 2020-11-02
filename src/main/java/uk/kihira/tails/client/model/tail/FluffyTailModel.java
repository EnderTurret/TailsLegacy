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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for the floofy tail everyone loves.
 */
public class FluffyTailModel extends PartModel {

	private final ModelRenderer tailBase;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail3;
	private final ModelRenderer tail4;
	private final ModelRenderer tail5;

	public FluffyTailModel() {
		tailBase = new ModelRenderer(this);
		tailBase.addBox(-1, -1, 0, 2, 2, 3);
		tailBase.setRotationPoint(0, 0, 0);
		setRotationDegrees(tailBase, -15F, 0, 0);

		tail1 = new ModelRenderer(this, 10, 0);
		tail1.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
		tail1.setRotationPoint(0, 0, 1.5F);
		setRotationDegrees(tail1, -15F, 0, 0);

		tail2 = new ModelRenderer(this, 0, 5);
		tail2.addBox(-2, -2, 0, 4, 4, 4);
		tail2.setRotationPoint(0, 0, 1.5F);
		setRotationDegrees(tail2, -15F, 0, 0);

		tail3 = new ModelRenderer(this, 0, 13);
		tail3.addBox(-2.5F, -2.5F, 0, 5, 5, 8);
		tail3.setRotationPoint(0, 0, 3F);
		setRotationDegrees(tail3, -25F, 0, 0);

		tail4 = new ModelRenderer(this, 0, 26);
		tail4.addBox(-2, -2, 0, 4, 4, 2);
		tail4.setRotationPoint(0, 0, 7.4F);
		setRotationDegrees(tail4, 15F, 0, 0);

		tail5 = new ModelRenderer(this, 12, 26);
		tail5.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
		tail5.setRotationPoint(0, 0, 1.4F);
		setRotationDegrees(tail5, 15F, 0, 0);

		tail4.addChild(tail5);
		tail3.addChild(tail4);
		tail2.addChild(tail3);
		tail1.addChild(tail2);
		tailBase.addChild(tail1);
	}

	public void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running
		if (entity.getRidingEntity() == null) {
			if (entity instanceof PlayerEntity) {
				final double[] angles = getMotionAngles((PlayerEntity) entity, partialTicks);
				xAngleOffset = angles[0];
				yAngleOffset = angles[1];
				zAngleOffset = angles[2];

				switch (subtype) {
				// Fox Tail
				case 0:
					xAngleOffset = MathHelper.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
					zAngleOffset = MathHelper.clamp(zAngleOffset, -0.5D, 0.5D);
					break;
					// Twin Tails
				case 1:
					xAngleOffset = MathHelper.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
					zAngleOffset = MathHelper.clamp(zAngleOffset, -0.5D, 0.5D);
					break;
					// Nine tails
				case 2:
					zAngleOffset = MathHelper.clamp(zAngleOffset * 0.5D, -1D, 0.5D);
					xAngleOffset = MathHelper.clamp(xAngleOffset * 0.25D, -1D, 0.2D);
					xAngleOffset += MathHelper.cos(timestep + xOffset) / 30F;
					break;
				}
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running
			}
		}
		// Mounted
		else
			switch (subtype) {
			case 0: // Fox Tail
				xAngleOffset = Math.toRadians(22F);
				yAngleMultiplier = 0.5F;
				break;
			case 1: // Twin Tails
				xAngleOffset = Math.toRadians(20F);
				yAngleMultiplier = 0.5F;
				break;
			case 2: // Nine Tails
				xAngleOffset = Math.toRadians(15F);
				yAngleMultiplier = 0.75F;
				break;
			}

		setRotationRadians(tailBase, xAngle + xAngleOffset, (-zAngleOffset / 2F + yAngle + MathHelper.cos(timestep + yOffset) / 8F) * yAngleMultiplier + yAngleOffset, -zAngleOffset / 8F);
		setRotationRadians(tail1, -0.2617993877991494 + xAngleOffset + Math.abs(zAngleOffset / 2F), (-zAngleOffset / 2F + MathHelper.cos(timestep - 1 + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
		setRotationRadians(tail2, -0.2617993877991494 + xAngleOffset / 2F, (-zAngleOffset / 2F + MathHelper.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
		setRotationRadians(tail3, -0.4363323129985824 + xAngleOffset / 2F, (-zAngleOffset / 2F + MathHelper.cos(timestep - 2 + yOffset) / 20F) * yAngleMultiplier, -zAngleOffset / 20F);
		setRotationRadians(tail4, 0.2617993877991494 - xAngleOffset / 2F, (-zAngleOffset / 2F + MathHelper.cos(timestep - 3 + yOffset) / 8F) * yAngleMultiplier, 0F);
		setRotationRadians(tail5, 0.2617993877991494 - xAngleOffset / 2.5F, (-zAngleOffset / 2F + MathHelper.cos(timestep - 4 + yOffset) / 8F) * yAngleMultiplier, 0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		float timestep = getAnimationTime(4000F, entity);

		if (subtype == 0) {
			this.setRotationAngles(0, timestep, 1F, 1F, 0, 0, partialTicks, entity);
			matrixStackIn.push();
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-20F));
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			matrixStackIn.pop();
		}
		else if (subtype == 1) {
			this.setRotationAngles(1, timestep, 1F, 1F, 0F, (float) Math.toRadians(40F), partialTicks, entity);
			matrixStackIn.push();
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-20F));
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(1, timestep, 1.4F, 0F, 0F, (float) Math.toRadians(-40F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			matrixStackIn.pop();
		}
		else if (subtype == 2) {
			timestep = getAnimationTime(6500F, entity);

			this.setRotationAngles(2, timestep, -1.5F, 2.5F, 0F, 0, partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -1.3F, 1.6F, 0, (float) Math.toRadians(30F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -1.1F, 0.7F, 0, (float) Math.toRadians(-30F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -1.2F, 2.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-15F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -0.9F, 1.1F, (float) Math.toRadians(20F), (float) Math.toRadians(15F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -0.8F, 2F, (float) Math.toRadians(20F), (float) Math.toRadians(45F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -1.25F, 0.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-45F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -1.4F, 0.9F, (float) Math.toRadians(45F), (float) Math.toRadians(15F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			this.setRotationAngles(2, timestep, -1.1F, 1.6F, (float) Math.toRadians(45F), (float) Math.toRadians(-15F), partialTicks, entity);
			tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}