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
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for devil tails.
 */
public class DevilTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;
	private final ModelPart tailTip;

	public DevilTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef
			.addOrReplaceChild("tailBase", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-1, -1, 0, 2, 2, 2), PartPose.rotation(rad(-30), 0, 0))
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(0, 4).addBox(-0.5F, -0.5F, 0, 1, 1, 4), PartPose.offsetAndRotation(0, 0, 1.8F, rad(-30), 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 9).addBox(-0.5F, -0.5F, 0, 1, 1, 5), PartPose.offsetAndRotation(0, 0, 3.8F, rad(-30), 0, 0))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 15).addBox(-0.5F, -0.5F, 0, 1, 1, 3), PartPose.offsetAndRotation(0, 0, 4.8F, rad(20), 0, 0))
			.addOrReplaceChild("tail4", CubeListBuilder.create()
					.texOffs(0, 19).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.offsetAndRotation(0, 0, 2.6F, rad(50), 0, 0))
			.addOrReplaceChild("tail5", CubeListBuilder.create()
					.texOffs(0, 22).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.offsetAndRotation(0, 0, 1.7F, rad(50), 0, 0))
			.addOrReplaceChild("tailTip", CubeListBuilder.create()
					.texOffs(12, 0).addBox(-2.5F, 0, 0, 5, 5, 0), PartPose.offsetAndRotation(0, 0, 1.8F, rad(120), 0, 0));

		root = rootDef.bake(64, 32);
		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		tail4 = tail3.getChild("tail4");
		tail5 = tail4.getChild("tail5");
		tailTip = tail5.getChild("tailTip");
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {
		final float seed = getAnimationTime(6000, entity);
		final float xseed = getAnimationTime(12000, entity);
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player) {
				final double[] angles = getMotionAngles((Player) entity, partialTicks);

				xAngleOffset = Mth.clamp(angles[0] / 3.5F, -1F, 0.275D);
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = Math.toRadians(13F);
			yAngleMultiplier = 0.25F;
		}

		setRotationRadians(tailBase, Math.toRadians(-30F) + xAngleOffset * 2F, Mth.cos(seed - 1) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail1, Math.toRadians(-30F) + xAngleOffset * 2F, Mth.cos(seed - 2) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail2, Math.toRadians(-30F) + xAngleOffset * 2F, Mth.cos(seed - 3) / 8F * yAngleMultiplier, 0F);
		setRotationRadians(tail3, Math.toRadians(20F) - xAngleOffset * 2F + Mth.cos(xseed - 4) / 6F * yAngleMultiplier, Mth.cos(seed - 4) / 8F * yAngleMultiplier, Mth.cos(xseed - 4) / 8F * yAngleMultiplier);
		setRotationRadians(tail4, Math.toRadians(50F) - xAngleOffset * 3F + Mth.cos(xseed - 5) / 8F * yAngleMultiplier, Mth.cos(seed - 5) / 8F * yAngleMultiplier, Mth.cos(xseed - 5) / 8F * yAngleMultiplier);
		setRotationRadians(tail5, Math.toRadians(50F) - xAngleOffset * 4F + Mth.cos(xseed - 6) / 4F  * yAngleMultiplier, Mth.cos(seed - 6) / 8F * yAngleMultiplier, Mth.cos(xseed - 6) / 8F * yAngleMultiplier);
		setRotationRadians(tailTip, Math.toRadians(120F) - xAngleOffset, 0F, 0F);
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		if (subtype == 1)
			tailTip.visible = false;

		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		tailTip.visible = true;
	}
}