/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.body;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.PartModel;
import uk.kihira.tails.common.client.part.Part;

/**
 * <p>The shark tail part model.</p>
 * <p>Model created by access_denied.</p>
 */
final class SharkTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.t$isPassenger()) {
			xAngleOffset = rad(12);
			yAngleMultiplier = 0.25;
		} else {
			final double[] angles = getMotionAngles(entity, partialTick);

			xAngleOffset = TailsMath.clamp(angles[0] / 5, -1, 0.45);
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
		}

		final float timestep = getAnimationTime(3000, entity);

		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		final TailsModelPart tail3 = tail2.t$getChild("tail3");
		final TailsModelPart finBase = tail3.t$getChild("finBase");
		setRotationRadians(tailBase, -rad(37.37) + xAngleOffset * 4, TailsMath.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1,     rad(0.08)  + xAngleOffset * 1, TailsMath.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail2,     rad(15.96) - xAngleOffset * 2, TailsMath.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail3,     rad(13.04) - xAngleOffset,     TailsMath.cos(timestep - 4) / 5 * yAngleMultiplier, 0);
		setRotationRadians(finBase,   rad(148.7),                    TailsMath.cos(timestep - 10) / 5 * yAngleMultiplier, 0);
	}
}
