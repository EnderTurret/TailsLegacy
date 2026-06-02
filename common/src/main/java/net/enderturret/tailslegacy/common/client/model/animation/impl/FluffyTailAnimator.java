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

public final class FluffyTailAnimator implements ModelAnimator {

	private final TailsModelPart[] tailBase = new TailsModelPart[9];
	private final TailsModelPart[] tail1 = new TailsModelPart[9];
	private final TailsModelPart[] tail2 = new TailsModelPart[9];
	private final TailsModelPart[] tail3 = new TailsModelPart[9];
	private final TailsModelPart[] tail4 = new TailsModelPart[9];
	private final TailsModelPart[] tail5 = new TailsModelPart[9];

	public FluffyTailAnimator(TailsModelPart model, JsonObject ignored) {
		for (int i = 0; i < 9; i++) {
			tailBase[i] = model.t$getChild("tailBase" + (i + 1));
			tail1[i] = tailBase[i].t$getChild("tail1");
			tail2[i] = tail1[i].t$getChild("tail2");
			tail3[i] = tail2[i].t$getChild("tail3");
			tail4[i] = tail3[i].t$getChild("tail4");
			tail5[i] = tail4[i].t$getChild("tail5");
		}
	}

	@Override
	public void setupAnim(@Nullable AnimatorStorage storage, TailsEntity entity, TailsModelPart model, SubType subType, float partialTick) {
		setupAnim(entity, 1, 0, partialTick, PartModelHelper.getAnimationTime(4000, entity), 1, 1, 0, 0);
	}

	public void setupAnim(TailsEntity entity, int subtype, float partialTick, float yOffset, float xOffset, double xAngle, double yAngle) {
		final float timestep;
		switch (subtype) {
			case 2: timestep = 6500; break;
			default: timestep = 4000; break;
		}
		setupAnim(entity, 1, subtype, partialTick, timestep, yOffset, xOffset, xAngle, yAngle);
	}

	public void setupAnim(TailsEntity entity, int index, int subtype, float partialTick, float timestep, float yOffset, float xOffset, double xAngle, double yAngle) {
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running
		if (entity.t$isPassenger()) {
			switch (subtype) {
				// Fox Tail
				case 0:
					xAngleOffset = PartModelHelper.rad(22);
					yAngleMultiplier = 0.5F;
					break;
				// Twin Tails
				case 1:
					xAngleOffset = PartModelHelper.rad(20);
					yAngleMultiplier = 0.5F;
					break;
				// Nine Tails
				case 2:
					xAngleOffset = PartModelHelper.rad(15);
					yAngleMultiplier = 0.75F;
					break;
			}
		} else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);
			xAngleOffset = angles[0];
			yAngleOffset = angles[1];
			zAngleOffset = angles[2];

			switch (subtype) {
				// Fox Tail; Twin Tails
				case 0:
				case 1:
					xAngleOffset = TailsMath.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
					zAngleOffset = TailsMath.clamp(zAngleOffset, -0.5D, 0.5D);
					break;
				// Nine tails
				case 2:
					zAngleOffset = TailsMath.clamp(zAngleOffset * 0.5D, -1D, 0.5D);
					xAngleOffset = TailsMath.clamp(xAngleOffset * 0.25D, -1D, 0.2D);
					xAngleOffset += TailsMath.cos(timestep + xOffset) / 30F;
					break;
			}
			yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running
		}

		tailBase[index - 1].t$setRotationRadians(xAngle + xAngleOffset,                      (yAngle + -zAngleOffset / 2F + TailsMath.cos(timestep + yOffset) / 8F)        * yAngleMultiplier + yAngleOffset, zAngleOffset / -8F);
		tail1[index - 1].t$setOffsetRotationRadians(      xAngleOffset + Math.abs(zAngleOffset / 2F), (-zAngleOffset / 2F + TailsMath.cos(timestep - 1 + yOffset) / 8F)    * yAngleMultiplier,                zAngleOffset / -8F);
		tail2[index - 1].t$setOffsetRotationRadians(      xAngleOffset / 2F,                          (-zAngleOffset / 2F + TailsMath.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier,                zAngleOffset / -8F);
		tail3[index - 1].t$setOffsetRotationRadians(      xAngleOffset / 2F,                          (-zAngleOffset / 2F + TailsMath.cos(timestep - 2 + yOffset) / 20F)   * yAngleMultiplier,                zAngleOffset / -20F);
		tail4[index - 1].t$setOffsetRotationRadians(      xAngleOffset / -2F,                         (-zAngleOffset / 2F + TailsMath.cos(timestep - 3 + yOffset) / 8F)    * yAngleMultiplier,                0F);
		tail5[index - 1].t$setOffsetRotationRadians(      xAngleOffset / -2.5F,                       (-zAngleOffset / 2F + TailsMath.cos(timestep - 4 + yOffset) / 8F)    * yAngleMultiplier,                0F);
	}
}