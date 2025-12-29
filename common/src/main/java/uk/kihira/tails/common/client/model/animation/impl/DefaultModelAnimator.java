/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.animation.impl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.PartModelHelper;
import uk.kihira.tails.common.client.model.animation.ModelAnimator;
import uk.kihira.tails.common.client.part.Part.SubType;
import uk.kihira.tails.common.client.part.PartPath;
import uk.kihira.tails.common.gson.TailsGsonHelper;

public final class DefaultModelAnimator implements ModelAnimator {

	private final int[] duration = new int[3];
	private boolean sameDuration;

	private Pose sittingPose;
	private Pose swimmingPose;
	private Pose sleepingPose;
	private Pose crouchingPose;

	private final MotionModifier xMotionOffset = new MotionModifier(0);
	private final MotionModifier yMotionOffset = new MotionModifier(1);
	private final MotionModifier zMotionOffset = new MotionModifier(2);
	private final MotionModifier xMotionMultiplier = new MotionModifier(0);
	private final MotionModifier yMotionMultiplier = new MotionModifier(1);
	private final MotionModifier zMotionMultiplier = new MotionModifier(2);

	private final List<PartEquations> equations = new ArrayList<>();
	private final List<PartCopy> copies = new ArrayList<>(0);

	private final float[] timesteps = new float[3];
	private final double[] angleOffsets = new double[3];
	private final double[] angleMultipliers = new double[3];

	public static ModelAnimator parse(TailsModelPart model, JsonObject obj) {
		final DefaultModelAnimator ret = new DefaultModelAnimator();

		final JsonElement rawDuration = obj.get("duration");
		if (rawDuration.isJsonArray())
			transferTo(rawDuration.getAsJsonArray(), ret.duration);
		else
			ret.duration[0] = ret.duration[1] = ret.duration[2] = rawDuration.getAsInt();

		if (ret.duration[0] == ret.duration[1] && ret.duration[0] == ret.duration[2])
			ret.sameDuration = true;

		if (obj.has("poses")) {
			final JsonObject poses = obj.getAsJsonObject("poses");
			if (poses.has("sitting")) ret.sittingPose = Pose.from(poses.getAsJsonObject("sitting"));
			if (poses.has("swimming")) ret.swimmingPose = Pose.from(poses.getAsJsonObject("swimming"));
			if (poses.has("sleeping")) ret.sleepingPose = Pose.from(poses.getAsJsonObject("sleeping"));
			if (poses.has("crouching")) ret.crouchingPose = Pose.from(poses.getAsJsonObject("crouching"));
		}

		final JsonObject motion = obj.getAsJsonObject("motion");

		ret.xMotionOffset.from(motion.get("x_offset"));
		ret.yMotionOffset.from(motion.get("y_offset"));
		ret.zMotionOffset.from(motion.get("z_offset"));
		ret.xMotionMultiplier.from(motion.get("x_multiplier"));
		ret.yMotionMultiplier.from(motion.get("y_multiplier"));
		ret.zMotionMultiplier.from(motion.get("z_multiplier"));

		final JsonObject parts = obj.getAsJsonObject("parts");

		for (Map.Entry<String, JsonElement> entry : parts.entrySet()) {
			final PartPath path = new PartPath(entry.getKey());
			final TailsModelPart part = path.traverse(model);
			if (entry.getValue().isJsonObject()) {
				final PartEquations eq = PartEquations.from(part, entry.getValue().getAsJsonObject());
				ret.equations.add(eq);
			} else
				ret.copies.add(new PartCopy(new PartPath(entry.getValue().getAsString()).traverse(model), part));
		}

		return ret;
	}

	private static void transferTo(JsonArray from, int[] to) {
		if (from.size() != to.length) throw new IllegalArgumentException("Expected exactly " + to.length + " elements, found " + from.size());
		for (int i = 0; i < to.length; i++)
			to[i] = from.get(i).getAsInt();
	}

	private static void transferTo(JsonArray from, double[] to) {
		if (from.size() != to.length) throw new IllegalArgumentException("Expected exactly " + to.length + " elements, found " + from.size());
		for (int i = 0; i < to.length; i++)
			to[i] = from.get(i).getAsDouble();
	}

	@Override
	public void setupAnim(TailsEntity entity, TailsModelPart model, SubType subType, float partialTick) {
		timesteps[0] = PartModelHelper.getAnimationTime(duration[0], entity);
		if (sameDuration) // Optimize for the common case.
			timesteps[2] = timesteps[1] = timesteps[0];
		else {
			timesteps[1] = PartModelHelper.getAnimationTime(duration[1], entity);
			timesteps[2] = PartModelHelper.getAnimationTime(duration[2], entity);
		}

		Pose activePose = null;
		if (entity.t$isPassenger()) activePose = sittingPose;
		else if (entity.t$isSwimmingPose()) activePose = swimmingPose;
		else if (entity.t$isSleepingPose()) activePose = sleepingPose;
		else if (entity.t$isCrouching()) activePose = crouchingPose;

		if (activePose != null) {
			System.arraycopy(activePose.offset, 0, angleOffsets, 0, 3);
			System.arraycopy(activePose.multiplier, 0, angleMultipliers, 0, 3);
		} else {
			PartModelHelper.getMotionAngles(entity, partialTick, angleOffsets);

			double tempX = xMotionOffset.apply(angleOffsets);
			double tempY = yMotionOffset.apply(angleOffsets);
			double tempZ = zMotionOffset.apply(angleOffsets);

			angleOffsets[0] = tempX; angleOffsets[1] = tempY; angleOffsets[2] = tempZ;

			angleMultipliers[0] = xMotionMultiplier.apply(angleOffsets);
			angleMultipliers[1] = yMotionMultiplier.apply(angleOffsets);
			angleMultipliers[2] = zMotionMultiplier.apply(angleOffsets);
		}

		for (PartEquations eq : equations)
			eq.rotate(timesteps, angleOffsets, angleMultipliers);

		for (PartCopy copy : copies)
			copy.rotate();
	}

	private static final class Pose {

		public final double[] offset = { 0, 0, 0 };
		public final double[] multiplier = { 1, 1, 1 };

		public static Pose from(JsonObject obj) {
			final Pose ret = new Pose();

			if (obj.has("offset")) transferTo(obj.get("offset").getAsJsonArray(), ret.offset);
			if (obj.has("multiplier")) transferTo(obj.get("multiplier").getAsJsonArray(), ret.multiplier);

			return ret;
		}
	}

	private static final class MotionModifier {

		public int angle;
		public double multiplier = 1;
		public double offset = 0;
		public double[] range = { -Double.MAX_VALUE, Double.MAX_VALUE };

		public MotionModifier(int defaultAngle) { angle = defaultAngle; }

		public double apply(double[] angles) {
			if (angle == -1) return 1;
			return TailsMath.clamp(angles[angle] * multiplier + offset, range[0], range[1]);
		}

		public void from(JsonElement elem) {
			if (elem == null || elem.isJsonNull()) {
				angle = -1;
				return;
			}

			final JsonObject obj = elem.getAsJsonObject();

			if (obj.has("angle")) angle = TailsMath.clamp(obj.get("angle").getAsInt(), 0, 2);
			if (obj.has("multiplier")) multiplier = obj.get("multiplier").getAsDouble();
			if (obj.has("offset")) offset = obj.get("offset").getAsDouble();
			if (obj.has("range")) transferTo(obj.getAsJsonArray("range"), range);
		}
	}

	private static final class PartEquations {

		public final TailsModelPart part;
		public final PartEquation x = new PartEquation();
		public final PartEquation y = new PartEquation();
		public final PartEquation z = new PartEquation();

		public PartEquations(TailsModelPart part) { this.part = part; }

		public void rotate(float[] timesteps, double[] angleOffsets, double[] angleMultipliers) {
			part.t$setOffsetRotationRadians(
					x.solve(timesteps[0], angleOffsets[0], angleMultipliers[0]),
					y.solve(timesteps[1], angleOffsets[1], angleMultipliers[1]),
					z.solve(timesteps[2], angleOffsets[2], angleMultipliers[2])
					);
		}

		public static PartEquations from(TailsModelPart part, JsonObject obj) {
			final PartEquations ret = new PartEquations(part);

			ret.x.from(obj, "x_");
			ret.y.from(obj, "y_");
			ret.z.from(obj, "z_");

			return ret;
		}
	}

	private static final class PartCopy {

		public final TailsModelPart from, to;

		public PartCopy(TailsModelPart from, TailsModelPart to) {
			this.from = from;
			this.to = to;
		}

		public void rotate() {
			to.t$setRotationRadians(from.t$getXRot(), from.t$getYRot(), from.t$getZRot());
		}
	}

	private static final class PartEquation {

		public double factor = 0;
		public int step = 0;
		public boolean offsetAbsolute = false;
		public double offsetFactor = 0;

		public double solve(float timestep, double angleOffset, double angleMultiplier) {
			if (offsetAbsolute) angleOffset = Math.abs(angleOffset);

			double value = 1;
			if (step != 0)
				value = TailsMath.cos(timestep - step);

			return value * angleMultiplier * factor + angleOffset * offsetFactor;
		}

		public void from(JsonObject obj, String prefix) {
			String key;
			if (obj.has(key = prefix + "factor")) factor = obj.get(key).getAsDouble();
			if (obj.has(key = prefix + "step")) step = obj.get(key).getAsInt();
			if (obj.has(key = prefix + "offset_absolute")) offsetAbsolute = obj.get(key).getAsBoolean();
			if (obj.has(key = prefix + "offset_factor")) offsetFactor = obj.get(key).getAsDouble();
		}
	}
}