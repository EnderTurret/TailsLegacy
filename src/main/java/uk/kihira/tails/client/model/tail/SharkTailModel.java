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
 * <p>The shark tail part model.</p>
 * <p>Model created by access_denied.</p>
 */
final class SharkTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = Mth.clamp(angles[0] / 5, -1, 0.45);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = rad(12);
			yAngleMultiplier = 0.25;
		}

		final float timestep = getAnimationTime(3000D, entity);

		final ModelPart tailBase = model.getChild("tailBase");
		final ModelPart tail1 = tailBase.getChild("tail1");
		final ModelPart tail2 = tail1.getChild("tail2");
		final ModelPart tail3 = tail2.getChild("tail3");
		final ModelPart finBase = tail3.getChild("finBase");
		setRotationRadians(tailBase, -rad(37.37) + xAngleOffset * 4, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(0.08) + xAngleOffset * 1, Mth.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(15.96) - xAngleOffset * 2, Mth.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail3, rad(13.04) - xAngleOffset, Mth.cos(timestep - 4) / 5 * yAngleMultiplier, 0);
		setRotationRadians(finBase, rad(148.7), Mth.cos(timestep - 10) / 5 * yAngleMultiplier, 0);
	}
}
