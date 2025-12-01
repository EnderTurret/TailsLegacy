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
 * <p>The bird tail part model.</p>
 * <p>Model created by blusunrize.</p>
 */
final class BirdTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double zAngleOffset = 0;

		if (entity.t$isPassenger())
			xAngleOffset = rad(60);
		else {
			final double[] angles = getMotionAngles(entity, partialTick);
			xAngleOffset = angles[0];
			zAngleOffset = angles[2];

			xAngleOffset -= Mth.cos(timestep - 1) / 15F;
			zAngleOffset -= Mth.cos(timestep - 1) / 25F;
			xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
			zAngleOffset = Mth.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
		}

		final TailsModelPart center = model.t$getChild("center");
		final TailsModelPart left0 = center.t$getChild("left0");
		final TailsModelPart left1 = left0.t$getChild("left1");
		final TailsModelPart left2 = left1.t$getChild("left2");
		final TailsModelPart right0 = center.t$getChild("right0");
		final TailsModelPart right1 = right0.t$getChild("right1");
		final TailsModelPart right2 = right1.t$getChild("right2");
		setRotationRadians(center, rad(50) + xAngleOffset, -zAngleOffset, 0);
		setRotationRadians(left0, rad(-2), rad(-5), rad(11) + xAngleOffset / 10);
		setRotationRadians(left1, rad(-2), rad(-7), xAngleOffset / 10);
		setRotationRadians(left2, rad(-2), rad(-10), rad(10) + xAngleOffset / 10);
		setRotationRadians(right0, rad(-2), rad(5), rad(-11) - xAngleOffset / 10);
		setRotationRadians(right1, rad(-2), rad(7), -xAngleOffset / 10);
		setRotationRadians(right2, rad(-2), rad(10), rad(-10) - xAngleOffset / 10);
	}
}