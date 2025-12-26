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
import uk.kihira.tails.common.client.model.PartModelHelper;
import uk.kihira.tails.common.client.part.Part;

/**
 * <p>The bird tail part model.</p>
 * <p>Model created by blusunrize.</p>
 */
final class BirdTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		final float timestep = PartModelHelper.getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double zAngleOffset = 0;

		if (entity.t$isPassenger())
			xAngleOffset = PartModelHelper.rad(60);
		else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);
			xAngleOffset = angles[0];
			zAngleOffset = angles[2];

			xAngleOffset -= TailsMath.cos(timestep - 1) / 15F;
			zAngleOffset -= TailsMath.cos(timestep - 1) / 25F;
			xAngleOffset = TailsMath.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
			zAngleOffset = TailsMath.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
		}

		final TailsModelPart center = model.t$getChild("center");
		final TailsModelPart left0 = center.t$getChild("left0");
		final TailsModelPart left1 = left0.t$getChild("left1");
		final TailsModelPart left2 = left1.t$getChild("left2");
		final TailsModelPart right0 = center.t$getChild("right0");
		final TailsModelPart right1 = right0.t$getChild("right1");
		final TailsModelPart right2 = right1.t$getChild("right2");
		center.t$setOffsetRotationRadians(xAngleOffset, -zAngleOffset, 0);
		left0.t$setOffsetRotationRadians(0, 0, xAngleOffset / 10);
		left1.t$setOffsetRotationRadians(0, 0, xAngleOffset / 10);
		left2.t$setOffsetRotationRadians(0, 0, xAngleOffset / 10);
		right0.t$setOffsetRotationRadians(0, 0, xAngleOffset / -10);
		right1.t$setOffsetRotationRadians(0, 0, xAngleOffset / -10);
		right2.t$setOffsetRotationRadians(0, 0, xAngleOffset / -10);
	}
}