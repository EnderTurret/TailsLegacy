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

final class ScorpionTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		final TailsModelPart tailBase = model.t$getChild("tailBase");

		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.t$isPassenger()) {
			xAngleOffset = 0;
			yAngleMultiplier = 0.25;
		} else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);

			xAngleOffset = TailsMath.clamp(angles[0] / 8, -0.1, 0.1);
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
		}

		final float yTimestep = PartModelHelper.getAnimationTime(16000, entity);
		final float zTimestep = PartModelHelper.getAnimationTime(12000, entity);

		tailBase.t$setOffsetRotationRadians(
				xAngleOffset * -2,
				TailsMath.cos(yTimestep - 1) / 12 * yAngleMultiplier,
				TailsMath.cos(zTimestep - 2) / 12 * yAngleMultiplier
				);
	}
}