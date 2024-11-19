/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.ModelSerializer;
import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * The raccoon tail part model.
 */
final class RaccoonTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
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

		final ModelPart tailBase = model.getChild("tailBase");
		final ModelPart tail1 = tailBase.getChild("tail1");
		final ModelPart tail2 = tail1.getChild("tail2");
		setRotationRadians(tailBase, xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
		setRotationRadians(tail1, rad(-40) + xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
		setRotationRadians(tail2, rad(-30) + xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
	}
}