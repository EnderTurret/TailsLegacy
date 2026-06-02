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

public final class RaccoonTailAnimator implements ModelAnimator {

	private final TailsModelPart tailBase;
	private final TailsModelPart tail1;
	private final TailsModelPart tail2;

	public RaccoonTailAnimator(TailsModelPart model, JsonObject ignored) {
		tailBase = model.t$getChild("tailBase");
		tail1 = tailBase.t$getChild("tail1");
		tail2 = tail1.t$getChild("tail2");
	}

	@Override
	public void setupAnim(@Nullable AnimatorStorage storage, TailsEntity entity, TailsModelPart model, SubType subType, float partialTick) {
		final float timestep = PartModelHelper.getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.

		if (entity.t$isPassenger()) {
			xAngleOffset = PartModelHelper.rad(20);
			yAngleMultiplier = 0.2F;
		} else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);

			xAngleOffset = angles[0];
			yAngleOffset = angles[1];
			zAngleOffset = angles[2];
			yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.

			xAngleOffset = TailsMath.clamp(xAngleOffset * 0.6, -1, 0.45);
			zAngleOffset = TailsMath.clamp(zAngleOffset * 0.5, -0.5, 0.5);
		}

		tailBase.t$setOffsetRotationRadians(xAngleOffset, (TailsMath.cos(timestep - 1) / 15 + yAngleOffset - zAngleOffset) * yAngleMultiplier, zAngleOffset / -4);
		tail1.t$setOffsetRotationRadians   (xAngleOffset, (TailsMath.cos(timestep - 1) / 15 + yAngleOffset - zAngleOffset) * yAngleMultiplier, zAngleOffset / -4);
		tail2.t$setOffsetRotationRadians   (xAngleOffset, (TailsMath.cos(timestep - 1) / 15 + yAngleOffset - zAngleOffset) * yAngleMultiplier, zAngleOffset / -4);
	}
}