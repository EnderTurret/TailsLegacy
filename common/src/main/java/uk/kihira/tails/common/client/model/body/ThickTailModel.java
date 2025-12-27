/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.body;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.PartModel;
import uk.kihira.tails.common.client.model.PartModelHelper;
import uk.kihira.tails.common.client.part.Part;

final class ThickTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		final TailsModelPart tail3 = tail2.t$getChild("tail3");
		final TailsModelPart tail4 = tail3.t$getChild("tail4");

		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.t$isPassenger()) {
			xAngleOffset = PartModelHelper.rad(4);
			yAngleMultiplier = 0.25;
		} else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);

			xAngleOffset = TailsMath.clamp(angles[0] / 8, -0.05, 0.05);
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
		}

		final float timestep = PartModelHelper.getAnimationTime(7000, entity);

		tailBase.t$setOffsetRotationRadians(xAngleOffset * 2,  TailsMath.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		tail1.t$setOffsetRotationRadians   (xAngleOffset * 2,  TailsMath.cos(timestep - 2) / 45 * yAngleMultiplier, 0);
		tail2.t$setOffsetRotationRadians   (xAngleOffset / -4, TailsMath.cos(timestep - 3) / 45 * yAngleMultiplier, 0);
		tail3.t$setOffsetRotationRadians   (xAngleOffset / -4, TailsMath.cos(timestep - 4) / 45 * yAngleMultiplier, 0);
		tail4.t$setOffsetRotationRadians   (xAngleOffset / -4, TailsMath.cos(timestep - 5) / 45 * yAngleMultiplier, 0);
	}
}