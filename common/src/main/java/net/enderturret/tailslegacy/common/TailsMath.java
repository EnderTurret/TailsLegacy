/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common;

public final class TailsMath {

	public static final float PI = (float) Math.PI;
    public static final float HALF_PI = (float) (Math.PI / 2);
    public static final float TWO_PI = (float) (Math.PI * 2);
	public static final float DEG_TO_RAD = (float) (Math.PI / 180D);
	public static final float RAD_TO_DEG = 180F / (float) Math.PI;

	public static final double DEG_TO_RAD_D = Math.PI / 180D;
	public static final double RAD_TO_DEG_D = 180D / Math.PI;

	public static float wrapDegrees(float value) {
		value = value % 360F;
		if (value >= 180F) value -= 360F;
		if (value < -180F) value += 360F;
		return value;
	}

	public static int clamp(int value, int min, int max) {
		return value < min ? min : Math.min(value, max);
	}

	public static float clamp(float value, float min, float max) {
		return value < min ? min : Math.min(value, max);
	}

	public static double clamp(double value, double min, double max) {
		return value < min ? min : Math.min(value, max);
	}

	public static float lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}

	public static double lerp(double delta, double start, double end) {
		return start + delta * (end - start);
	}

	public static float rotLerp(float delta, float start, float end) {
		return start + delta * wrapDegrees(end - start);
	}

	public static float sin(float angle) {
		return TailsPlatform.get().lookupSin(angle);
	}

	public static float cos(float angle) {
		return TailsPlatform.get().lookupCos(angle);
	}
}