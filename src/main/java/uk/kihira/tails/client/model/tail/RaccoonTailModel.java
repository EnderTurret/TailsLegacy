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
 * The model for raccoon tails.
 */
public class RaccoonTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;

	public RaccoonTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef.addOrReplaceChild("tailBase", CubeListBuilder.create().texOffs(12, 16).addBox(-1, -1, 0, 2, 2, 2), PartPose.ZERO)
			.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -1.5F, 0, 3, 3, 3), PartPose.offsetAndRotation(0, 0, 1, radf(-40), 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 0).addBox(-2, -2, 0, 4, 4, 12), PartPose.offsetAndRotation(0, 0, 2, radf(-30), 0, 0))
			.addOrReplaceChild("tailTip", CubeListBuilder.create().texOffs(0, 22).addBox(-1.5F, -1.5F, 0, 3, 3, 1), PartPose.offset(0, 0, 12));

		root = rootDef.bake(64, 32);

		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");

		config = new PartConfiguration(tailBase, List.of(tailBase, tail1, tail2));
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, Part.SubType subType, float headPitch) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.

		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = angles[0];
				yAngleOffset = angles[1];
				zAngleOffset = angles[2];
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.

				xAngleOffset = Mth.clamp(xAngleOffset * 0.6, -1, 0.45);
				zAngleOffset = Mth.clamp(zAngleOffset * 0.5, -0.5, 0.5);
			}
		}
		// Mounted
		else {
			xAngleOffset = rad(20);
			yAngleMultiplier = 0.2F;
		}

		setRotationRadians(tailBase, xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
		setRotationRadians(tail1, rad(-40) + xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
		setRotationRadians(tail2, rad(-30) + xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}