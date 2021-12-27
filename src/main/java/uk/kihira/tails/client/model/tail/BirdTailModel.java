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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for bird tails.
 */
public class BirdTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart center;
	private final ModelPart left0;
	private final ModelPart left1;
	private final ModelPart left2;
	private final ModelPart right0;
	private final ModelPart right2;
	private final ModelPart right1;

	public BirdTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot()
				.addOrReplaceChild("center", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-1.5F, -0.5F, -0.5F, 3, 9, 1), PartPose.offsetAndRotation(0, 0, 1, rad(55), 0, 0));

		final PartDefinition centerDef = rootDef.getChild("center")
				.addOrReplaceChild("left0", CubeListBuilder.create()
						.texOffs(0, 10).addBox(-1, 0, -0.5F, 2, 8, 1), PartPose.offsetAndRotation(-1, 0.5F, 0, rad(-2), rad(-8), rad(11)))
				.addOrReplaceChild("right0", CubeListBuilder.create()
						.mirror().texOffs(0, 10).addBox(-1, 0, -0.5F, 2, 8, 1), PartPose.offsetAndRotation(1, 0.5F, 0, rad(-2), rad(8), rad(-11)));

		final PartDefinition left0Def = centerDef.getChild("left0")
				.addOrReplaceChild("left1", CubeListBuilder.create()
						.texOffs(0, 19).addBox(-1, 0, -0.5F, 2, 7, 1), PartPose.offsetAndRotation(-1.5F, 0, 0, 0, rad(-6), 0));

		final PartDefinition left1Def = left0Def.getChild("left1")
				.addOrReplaceChild("left2", CubeListBuilder.create()
						.texOffs(6, 19).addBox(-0.5F, 0F, -0.5F, 1, 6, 1), PartPose.offsetAndRotation(-0.5F, 0, 0, 0, rad(-6), rad(15)));

		final PartDefinition right0Def = centerDef.getChild("right0")
				.addOrReplaceChild("right1", CubeListBuilder.create()
						.texOffs(0, 19).addBox(-1, 0, -0.5F, 2, 7, 1), PartPose.offsetAndRotation(1.5F, 0, 0, 0, rad(6), 0));

		final PartDefinition right1Def = right0Def.getChild("right1")
				.addOrReplaceChild("right2", CubeListBuilder.create()
						.texOffs(6, 19).addBox(-0.5F, 0, -0.5F, 1, 6, 1), PartPose.offsetAndRotation(0.5F, 0, 0, 0, rad(6), rad(-15)));

		root = rootDef.bake(64, 32);
		center = root.getChild("center");
		left0 = center.getChild("left0");
		left1 = left0.getChild("left1");
		left2 = left1.getChild("left2");
		right0 = center.getChild("right0");
		right1 = center.getChild("right1");
		right2 = center.getChild("right2");
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double zAngleOffset = 0;

		if (entity.getVehicle() == null) {
			if (entity instanceof Player) {
				final double[] angles = getMotionAngles((Player) entity, partialTicks);
				xAngleOffset = angles[0];
				zAngleOffset = angles[2];

				xAngleOffset -= Mth.cos(timestep - 1) / 15F;
				zAngleOffset -= Mth.cos(timestep - 1) / 25F;
				xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
				zAngleOffset = Mth.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
			}
		}
		// Mounted
		else
			xAngleOffset = Math.toRadians(60F);

		setRotationRadians(center, Math.toRadians(50) + xAngleOffset, -zAngleOffset, 0);
		setRotationRadians(left0, Math.toRadians(-2F), Math.toRadians(-5), Math.toRadians(11) + xAngleOffset / 10F);
		setRotationRadians(left1, Math.toRadians(-2F), Math.toRadians(-7), xAngleOffset / 10F);
		setRotationRadians(left2, Math.toRadians(-2F), Math.toRadians(-10), Math.toRadians(10) + xAngleOffset / 10F);
		setRotationRadians(right0, Math.toRadians(-2F), Math.toRadians(5), Math.toRadians(-11) - xAngleOffset / 10F);
		setRotationRadians(right1, Math.toRadians(-2F), Math.toRadians(7), -xAngleOffset / 10F);
		setRotationRadians(right2, Math.toRadians(-2F), Math.toRadians(10), Math.toRadians(-10) - xAngleOffset / 10F);
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		center.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}