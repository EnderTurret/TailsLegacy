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
import uk.kihira.tails.client.part.Part;

final class ScorpionTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = Mth.clamp(angles[0] / 8, -0.1, 0.1);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = 0;
			yAngleMultiplier = 0.25;
		}

		final float timestep1 = getAnimationTime(16000, entity);
		final float timestep2 = getAnimationTime(12000, entity);

		final ModelPart tailBase = model.getChild("tailBase");

		setRotationRadians(tailBase,
				tailBase.getInitialPose().xRot - 0.15 - xAngleOffset * 2,
				Mth.cos(timestep1 - 1) / 12 * yAngleMultiplier,
				Mth.cos(timestep2 - 2) / 12 * yAngleMultiplier);
	}
}