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
 * <p>The dragon tail part model.</p>
 * <p>Model created by TTFTCUTS.</p>
 */
final class DragonTailModel extends PartModel {

	@Override
	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.t$isPassenger()) {
			xAngleOffset = rad(12);
			yAngleMultiplier = 0.25;
		} else if (entity.t$isSwimmingPose())
			xAngleOffset = -0.1;
		else if (entity.t$isSleepingPose()) {
			xAngleOffset = -0.1;
			yAngleMultiplier = 0;
		} else {
			final double[] angles = getMotionAngles(entity, partialTick);

			xAngleOffset = TailsMath.clamp(angles[0] / 5, -1, 0.45);
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
		}

		final float timestep = getAnimationTime(4000, entity);

		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		final TailsModelPart tail3 = tail2.t$getChild("tail3");
		setRotationRadians(tailBase, rad(-40) + xAngleOffset * 2,                                  TailsMath.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1,    rad(-8)  + xAngleOffset * 2,                                  TailsMath.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail2,    rad(10)  - xAngleOffset / 4,                                  TailsMath.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail3,    rad(20)  + (xAngleOffset < 0 ? xAngleOffset : -xAngleOffset), TailsMath.cos(timestep - 4) / 5 * yAngleMultiplier, 0);

		final TailsModelPart tailSubBase = model.t$getChild("tailSubBase");
		final TailsModelPart tailSub1 = tailSubBase.t$getChild("tailSub1");
		final TailsModelPart tailSub2 = tailSub1.t$getChild("tailSub2");
		final TailsModelPart tailSub3 = tailSub2.t$getChild("tailSub3");
		if ("dragon_tail".equals(subType.id()) || "finned_dragon_tail".equals(subType.id())) {
			setRotationRadians(tailSubBase, tailBase.t$getXRot(), tailBase.t$getYRot(), tailBase.t$getZRot());
			setRotationRadians(tailSub1, tail1.t$getXRot(), tail1.t$getYRot(), tail1.t$getZRot());
			setRotationRadians(tailSub2, tail2.t$getXRot(), tail2.t$getYRot(), tail2.t$getZRot());
			setRotationRadians(tailSub3, tail3.t$getXRot(), tail3.t$getYRot(), tail3.t$getZRot());
		}
	}
}