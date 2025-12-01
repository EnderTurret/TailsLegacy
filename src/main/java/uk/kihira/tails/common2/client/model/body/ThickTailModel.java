/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.model.body;

import net.minecraft.util.Mth;

import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.PartModel;
import uk.kihira.tails.common2.client.part.Part;

final class ThickTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.t$isPassenger()) {
			xAngleOffset = rad(4);
			yAngleMultiplier = 0.25;
		} else {
			final double[] angles = getMotionAngles(entity, partialTick);

			xAngleOffset = Mth.clamp(angles[0] / 8, -0.05, 0.05);
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
		}

		final float timestep = getAnimationTime(7000, entity);

		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		final TailsModelPart tail3 = tail2.t$getChild("tail3");
		final TailsModelPart tail4 = tail3.t$getChild("tail4");

		setRotationRadians(tailBase, tailBase.t$getInitialXRot() + xAngleOffset * 2, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1,    tail1.t$getInitialXRot()    + xAngleOffset * 2, Mth.cos(timestep - 2) / 45 * yAngleMultiplier, 0);
		setRotationRadians(tail2,    tail2.t$getInitialXRot()    - xAngleOffset / 4, Mth.cos(timestep - 3) / 45 * yAngleMultiplier, 0);
		setRotationRadians(tail3,    tail3.t$getInitialXRot()    - xAngleOffset / 4, Mth.cos(timestep - 4) / 45 * yAngleMultiplier, 0);
		setRotationRadians(tail4,    tail4.t$getInitialXRot()    - xAngleOffset / 4, Mth.cos(timestep - 5) / 45 * yAngleMultiplier, 0);
	}
}