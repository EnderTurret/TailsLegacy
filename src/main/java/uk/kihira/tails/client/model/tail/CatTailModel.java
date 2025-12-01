/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
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

/**
 * The cat tail part model. (Not to be confused with cattails.)
 */
final class CatTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
		final float seed = getAnimationTime(6000, entity);
		final float xseed = getAnimationTime(12000, entity);
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = Mth.clamp(angles[0] / 3.5, -1F, 0.33);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		else {
			xAngleOffset = rad(13);
			yAngleMultiplier = 0.25;
		}

		final ModelPart tailBase = model.getChild("tailBase");
		final ModelPart tail1 = tailBase.getChild("tail1");
		final ModelPart tail2 = tail1.getChild("tail2");
		final ModelPart tail3 = tail2.getChild("tail3");
		final ModelPart tail4 = tail3.getChild("tail4");
		final ModelPart tail5 = tail4.getChild("tail5");
		setRotationRadians(tailBase, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 1) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 2) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 3) / 8 * yAngleMultiplier, Mth.cos(xseed - 3) / 16);
		setRotationRadians(tail3, rad(20) - xAngleOffset * 2 + Mth.cos(xseed - 4) / 8, Mth.cos(seed - 4) / 8 * yAngleMultiplier, Mth.cos(xseed - 4) / 8);
		setRotationRadians(tail4, rad(50) - xAngleOffset * 2.5 + Mth.cos(xseed - 5) / 10, Mth.cos(seed - 5) / 8 * yAngleMultiplier, Mth.cos(xseed - 5) / 8);
		setRotationRadians(tail5, rad(50) - xAngleOffset * 3 + Mth.cos(xseed - 6) / 10, Mth.cos(seed - 6) / 8 * yAngleMultiplier, Mth.cos(xseed - 6) / 8);
	}
}
