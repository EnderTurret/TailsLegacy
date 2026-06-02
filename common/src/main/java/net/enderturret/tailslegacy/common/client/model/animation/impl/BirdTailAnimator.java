/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model.animation.impl;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.PartModelHelper;
import net.enderturret.tailslegacy.common.client.model.animation.AnimatorStorage;
import net.enderturret.tailslegacy.common.client.model.animation.ModelAnimator;
import net.enderturret.tailslegacy.common.client.part.Part.SubType;

public final class BirdTailAnimator implements ModelAnimator {

	private final TailsModelPart center;
	private final TailsModelPart left0;
	private final TailsModelPart left1;
	private final TailsModelPart left2;
	private final TailsModelPart right0;
	private final TailsModelPart right1;
	private final TailsModelPart right2;

	public BirdTailAnimator(TailsModelPart model, JsonObject ignored) {
		center = model.t$getChild("center");
		left0 = center.t$getChild("left0");
		left1 = left0.t$getChild("left1");
		left2 = left1.t$getChild("left2");
		right0 = center.t$getChild("right0");
		right1 = right0.t$getChild("right1");
		right2 = right1.t$getChild("right2");
	}

	@Override
	public void setupAnim(@Nullable AnimatorStorage storage, TailsEntity entity, TailsModelPart model, SubType subType, float partialTick) {
		final float timestep = PartModelHelper.getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double zAngleOffset = 0;

		if (entity.t$isPassenger())
			xAngleOffset = PartModelHelper.rad(60);
		else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);
			xAngleOffset = angles[0] - TailsMath.cos(timestep - 1) / 15D;
			zAngleOffset = angles[2] - TailsMath.cos(timestep - 1) / 25D;

			xAngleOffset = TailsMath.clamp(xAngleOffset * 0.6, -1, 0.45);
			zAngleOffset = TailsMath.clamp(zAngleOffset * 0.5, -0.5, 0.5);
		}

		center.t$setOffsetRotationRadians(xAngleOffset, -zAngleOffset, 0);
		left0.t$setOffsetRotationRadians(0, 0, xAngleOffset / 10);
		left1.t$setOffsetRotationRadians(0, 0, xAngleOffset / 10);
		left2.t$setOffsetRotationRadians(0, 0, xAngleOffset / 10);
		right0.t$setOffsetRotationRadians(0, 0, xAngleOffset / -10);
		right1.t$setOffsetRotationRadians(0, 0, xAngleOffset / -10);
		right2.t$setOffsetRotationRadians(0, 0, xAngleOffset / -10);
	}
}