/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.model.body;

import net.minecraft.util.Mth;

import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.PartModel;
import uk.kihira.tails.common2.client.part.Part;

/**
 * The raccoon tail part model.
 */
final class RaccoonTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.

		if (entity.t$isPassenger()) {
			xAngleOffset = rad(20);
			yAngleMultiplier = 0.2F;
		} else {
			final double[] angles = getMotionAngles(entity, partialTick);

			xAngleOffset = angles[0];
			yAngleOffset = angles[1];
			zAngleOffset = angles[2];
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.

			xAngleOffset = Mth.clamp(xAngleOffset * 0.6, -1, 0.45);
			zAngleOffset = Mth.clamp(zAngleOffset * 0.5, -0.5, 0.5);
		}

		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		setRotationRadians(tailBase, xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
		setRotationRadians(tail1, rad(-40) + xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
		setRotationRadians(tail2, rad(-30) + xAngleOffset, (-zAngleOffset + Mth.cos(timestep - 1) / 15 + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4);
	}
}