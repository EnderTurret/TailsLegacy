/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.math;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

import uk.kihira.tails.common.TailsMath;

public final class TailsVec4d {

	private final double x;
	private final double y;
	private final double z;
	private final double w;

	public static final TailsVec4d ZERO = new TailsVec4d(0, 0, 0, 1);

	public TailsVec4d(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public TailsVec4d(double value) {
		this(value, value, value, value);
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	public double w() {
		return w;
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public TailsVec4d add(TailsVec3d other) {
		return new TailsVec4d(x + other.x(), y + other.y(), z + other.z(), w);
	}

	public TailsVec4d add(TailsVec4d other) {
		return new TailsVec4d(x + other.x, y + other.y, z + other.z, w + other.w);
	}

	public TailsVec4d add(double other) {
		return new TailsVec4d(x + other, y + other, z + other, w + other);
	}

	public TailsVec4d multiply(TailsVec4d other) {
		return new TailsVec4d(x * other.x, y * other.y, z * other.z, w * other.w);
	}

	public TailsVec4d scale(double factor) {
		return new TailsVec4d(x * factor, y * factor, z * factor, w * factor);
	}

	public TailsVec4d apply(DoubleUnaryOperator op) {
		return new TailsVec4d(op.applyAsDouble(x), op.applyAsDouble(y), op.applyAsDouble(z), op.applyAsDouble(w));
	}

	public TailsVec4d normalize() {
		final double len = length();

		if (len > 0) return scale(1 / len);

		return this;
	}

	public TailsVec4d lerp(TailsVec4d to, TailsVec4d delta) {
		return new TailsVec4d(
				TailsMath.lerp(delta.x, x, to.x),
				TailsMath.lerp(delta.y, y, to.y),
				TailsMath.lerp(delta.z, z, to.z),
				TailsMath.lerp(delta.w, w, to.w));
	}

	public TailsVec4d lerp(TailsVec4d to, double delta) {
		return new TailsVec4d(
				TailsMath.lerp(delta, x, to.x),
				TailsMath.lerp(delta, y, to.y),
				TailsMath.lerp(delta, z, to.z),
				TailsMath.lerp(delta, w, to.w));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TailsVec4d)) return false;
		final TailsVec4d vec = (TailsVec4d) obj;
		return x == vec.x && y == vec.y && z == vec.z && w == vec.w;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, w);
	}
}