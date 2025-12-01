/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common2.client.part.Part;

final class ThickTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = Mth.clamp(angles[0] / 8, -0.05, 0.05);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = rad(4);
			yAngleMultiplier = 0.25;
		}

		final float timestep = getAnimationTime(7000, entity);

		final ModelPart tailBase = model.getChild("tailBase");
		final ModelPart tail1 = tailBase.getChild("tail1");
		final ModelPart tail2 = tail1.getChild("tail2");
		final ModelPart tail3 = tail2.getChild("tail3");
		final ModelPart tail4 = tail3.getChild("tail4");

		setRotationRadians(tailBase, tailBase.getInitialPose().xRot + xAngleOffset * 2, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1,    tail1.getInitialPose().xRot    + xAngleOffset * 2, Mth.cos(timestep - 2) / 45 * yAngleMultiplier, 0);
		setRotationRadians(tail2,    tail2.getInitialPose().xRot    - xAngleOffset / 4, Mth.cos(timestep - 3) / 45 * yAngleMultiplier, 0);
		setRotationRadians(tail3,    tail3.getInitialPose().xRot    - xAngleOffset / 4, Mth.cos(timestep - 4) / 45 * yAngleMultiplier, 0);
		setRotationRadians(tail4,    tail4.getInitialPose().xRot    - xAngleOffset / 4, Mth.cos(timestep - 5) / 45 * yAngleMultiplier, 0);

		tailBase.xScale = tailBase.yScale = tailBase.zScale = 1.2f;
	}
}