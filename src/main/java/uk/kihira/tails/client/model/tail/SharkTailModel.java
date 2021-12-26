/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for shark tails.
 */
public class SharkTailModel extends PartModel {

	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart finBase;
	private final ModelPart finTop1;
	private final ModelPart finBot1;
	private final ModelPart finTop2;
	private final ModelPart finTop3;
	private final ModelPart finBot2;
	private final ModelPart fubBot3;

	public SharkTailModel() {
		texWidth = 64;
		texHeight = 32;
		finBot1 = new ModelPart(this, 26, 27);
		finBot1.setPos(-0.5F, -0.4F, -4.0F);
		finBot1.addBox(0.0F, 0.0F, -2.0F, 1, 3, 2, 0.0F);
		setRotateAngle(finBot1, 0.091106186954104F, 0.0F, 0.0F);
		tail1 = new ModelPart(this, 0, 16);
		tail1.setPos(0.0F, 0.0F, 3.5F);
		tail1.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 5, 0.0F);
		setRotateAngle(tail1, 0.0013962634015954637F, 0.0F, 0.0F);
		tailBase = new ModelPart(this, 0, 24);
		tailBase.setPos(0.0F, 0.5F, -0.6F);
		tailBase.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 4, 0.0F);
		setRotateAngle(tailBase, -0.6522295414702809F, 0.02949606435870417F, 0.0F);
		tail2 = new ModelPart(this, 0, 9);
		tail2.setPos(0.0F, 0.0F, 4.5F);
		tail2.addBox(-1.0F, -1.0F, -0.2F, 2, 2, 5, 0.0F);
		setRotateAngle(tail2, 0.278554548618295F, 0.0F, 0.0F);
		finTop1 = new ModelPart(this, 16, 10);
		finTop1.setPos(0.0F, 6.5F, -0.1F);
		finTop1.addBox(-0.5F, 0.0F, -2.9F, 1, 2, 3, 0.0F);
		setRotateAngle(finTop1, -0.091106186954104F, 0.0F, 0.0F);
		finTop3 = new ModelPart(this, 16, 1);
		finTop3.setPos(0.0F, 4.0F, 0.0F);
		finTop3.addBox(0.0F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
		setRotateAngle(finTop3, -0.136659280431156F, 0.0F, 0.0F);
		finBase = new ModelPart(this, 16, 21);
		finBase.setPos(0.0F, 0.0F, 3.0F);
		finBase.addBox(-0.5F, -0.4F, -4.0F, 1, 7, 4, 0.0F);
		setRotateAngle(finBase, 2.5953045977155678F, -0.0F, 0.0F);
		finBot2 = new ModelPart(this, 26, 21);
		finBot2.setPos(0.0F, 0.0F, -2.0F);
		finBot2.addBox(0.0F, 0.0F, -3.0F, 1, 3, 3, 0.0F);
		setRotateAngle(finBot2, 0.136659280431156F, -0.0F, 0.0F);
		tail3 = new ModelPart(this, 0, 3);
		tail3.setPos(0.0F, 0.0F, 4.4F);
		tail3.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
		setRotateAngle(tail3, 0.22759093446006054F, 0.0F, 0.0F);
		finTop2 = new ModelPart(this, 16, 4);
		finTop2.setPos(-0.5F, 2.0F, 0.1F);
		finTop2.addBox(0.0F, 0.0F, -2.0F, 1, 4, 2, 0.0F);
		setRotateAngle(finTop2, -0.136659280431156F, 0.0F, 0.0F);
		fubBot3 = new ModelPart(this, 26, 17);
		fubBot3.setPos(0.0F, 0.0F, -3.0F);
		fubBot3.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
		setRotateAngle(fubBot3, 0.1980948701013564F, -0.0F, 0.0F);
		finBase.addChild(finBot1);
		tailBase.addChild(tail1);
		tail1.addChild(tail2);
		finBase.addChild(finTop1);
		finTop2.addChild(finTop3);
		tail3.addChild(finBase);
		finBot1.addChild(finBot2);
		tail2.addChild(tail3);
		finTop1.addChild(finTop2);
		finBot2.addChild(fubBot3);
	}

	/**
	 * A helper function from Tabula to set the rotation of model parts.
	 * @param modelRenderer The {@link ModelPart} to set rotation angles of.
	 * @param x The x rotation angle.
	 * @param y The y rotation angle.
	 * @param z The z rotation angle.
	 */
	public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player) {
				final double[] angles = getMotionAngles((Player) entity, partialTicks);

				xAngleOffset = Mth.clamp(angles[0] / 5F, -1D, 0.45D);
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = Math.toRadians(12F);
			yAngleMultiplier = 0.25F;
		}

		final float timestep = getAnimationTime(3000D, entity);
		setRotationRadians(tailBase, -0.6522295414702809F + xAngleOffset * 4F, Mth.cos(timestep - 1) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail1, 0.0013962634015954637F + xAngleOffset * 1F, Mth.cos(timestep - 2) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail2, 0.278554548618295F - xAngleOffset * 2F, Mth.cos(timestep - 3) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail3, 0.22759093446006054F - xAngleOffset, Mth.cos(timestep - 4) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(finBase, 2.5953045977155678F, Mth.cos(timestep - 10) / 5F * yAngleMultiplier, 0F);

		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
