/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

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

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part;

/**
 * The model for bird tails.
 */
public final class BirdTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart center;
	private final ModelPart left0;
	private final ModelPart left1;
	private final ModelPart left2;
	private final ModelPart right0;
	private final ModelPart right2;
	private final ModelPart right1;

	public BirdTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition centerDef = rootDef.addOrReplaceChild("center", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.5F, -0.5F, -0.5F, 3, 9, 1), PartPose.offsetAndRotation(0, 0, 1, radf(55), 0, 0));

		centerDef
			.addOrReplaceChild("left0", CubeListBuilder.create()
					.texOffs(0, 10).addBox(-1, 0, -0.5F, 2, 8, 1), PartPose.offsetAndRotation(-1, 0.5F, 0, radf(-2), radf(-8), radf(11)))
			.addOrReplaceChild("left1", CubeListBuilder.create()
					.texOffs(0, 19).addBox(-1, 0, -0.5F, 2, 7, 1), PartPose.offsetAndRotation(-1.5F, 0, 0, 0, radf(-6), 0))
			.addOrReplaceChild("left2", CubeListBuilder.create()
					.texOffs(6, 19).addBox(-0.5F, 0F, -0.5F, 1, 6, 1), PartPose.offsetAndRotation(-0.5F, 0, 0, 0, radf(-6), radf(15)));

		centerDef
			.addOrReplaceChild("right0", CubeListBuilder.create()
					.mirror().texOffs(0, 10).addBox(-1, 0, -0.5F, 2, 8, 1), PartPose.offsetAndRotation(1, 0.5F, 0, radf(-2), radf(8), radf(-11)))
			.addOrReplaceChild("right1", CubeListBuilder.create()
					.texOffs(0, 19).addBox(-1, 0, -0.5F, 2, 7, 1), PartPose.offsetAndRotation(1.5F, 0, 0, 0, radf(6), 0))
			.addOrReplaceChild("right2", CubeListBuilder.create()
					.texOffs(6, 19).addBox(-0.5F, 0, -0.5F, 1, 6, 1), PartPose.offsetAndRotation(0.5F, 0, 0, 0, radf(6), radf(-15)));

		root = rootDef.bake(32, 32);

		center = root.getChild("center");
		left0 = center.getChild("left0");
		left1 = left0.getChild("left1");
		left2 = left1.getChild("left2");
		right0 = center.getChild("right0");
		right1 = right0.getChild("right1");
		right2 = right1.getChild("right2");

		config = new PartConfiguration(center, List.of(center, left0, left1, left2, right0, right1, right2))
				.setParents(left0, center)
				.setParents(left1, center, left0)
				.setParents(left2, center, left0, left1)
				.setParents(right0, center)
				.setParents(right1, center, right0)
				.setParents(right2, center, right0, right1);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, Part.SubType subType, float headPitch) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double zAngleOffset = 0;

		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);
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
			xAngleOffset = rad(60);

		setRotationRadians(center, rad(50) + xAngleOffset, -zAngleOffset, 0);
		setRotationRadians(left0, rad(-2), rad(-5), rad(11) + xAngleOffset / 10);
		setRotationRadians(left1, rad(-2), rad(-7), xAngleOffset / 10);
		setRotationRadians(left2, rad(-2), rad(-10), rad(10) + xAngleOffset / 10);
		setRotationRadians(right0, rad(-2), rad(5), rad(-11) - xAngleOffset / 10);
		setRotationRadians(right1, rad(-2), rad(7), -xAngleOffset / 10);
		setRotationRadians(right2, rad(-2), rad(10), rad(-10) - xAngleOffset / 10);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		center.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}