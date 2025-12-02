/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
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
 * The cat tail part model. (Not to be confused with cattails.)
 */
final class CatTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		final float seed = getAnimationTime(6000, entity);
		final float xseed = getAnimationTime(12000, entity);
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.t$isPassenger()) {
			xAngleOffset = rad(13);
			yAngleMultiplier = 0.25;
		} else {
			final double[] angles = getMotionAngles(entity, partialTick);

			xAngleOffset = TailsMath.clamp(angles[0] / 3.5, -1F, 0.33);
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
		}

		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		final TailsModelPart tail3 = tail2.t$getChild("tail3");
		final TailsModelPart tail4 = tail3.t$getChild("tail4");
		final TailsModelPart tail5 = tail4.t$getChild("tail5");
		setRotationRadians(tailBase, rad(-30) + xAngleOffset * 2, TailsMath.cos(seed - 1) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(-30) + xAngleOffset * 2, TailsMath.cos(seed - 2) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(-30) + xAngleOffset * 2, TailsMath.cos(seed - 3) / 8 * yAngleMultiplier, TailsMath.cos(xseed - 3) / 16);
		setRotationRadians(tail3, rad(20) - xAngleOffset * 2 + TailsMath.cos(xseed - 4) / 8, TailsMath.cos(seed - 4) / 8 * yAngleMultiplier, TailsMath.cos(xseed - 4) / 8);
		setRotationRadians(tail4, rad(50) - xAngleOffset * 2.5 + TailsMath.cos(xseed - 5) / 10, TailsMath.cos(seed - 5) / 8 * yAngleMultiplier, TailsMath.cos(xseed - 5) / 8);
		setRotationRadians(tail5, rad(50) - xAngleOffset * 3 + TailsMath.cos(xseed - 6) / 10, TailsMath.cos(seed - 6) / 8 * yAngleMultiplier, TailsMath.cos(xseed - 6) / 8);
	}
}
