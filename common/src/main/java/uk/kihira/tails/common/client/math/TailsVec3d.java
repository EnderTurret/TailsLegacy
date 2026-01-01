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

public final class TailsVec3d {

	private final double x;
	private final double y;
	private final double z;

	public static final TailsVec3d ZERO = new TailsVec3d(0, 0, 0);

	public TailsVec3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public TailsVec3d(double value) {
		this(value, value, value);
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

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public TailsVec3d add(TailsVec3d other) {
		return new TailsVec3d(x + other.x(), y + other.y(), z + other.z());
	}

	public TailsVec3d add(double other) {
		return new TailsVec3d(x + other, y + other, z + other);
	}

	public TailsVec3d multiply(TailsVec3d other) {
		return new TailsVec3d(x * other.x, y * other.y, z * other.z);
	}

	public TailsVec3d scale(double factor) {
		return new TailsVec3d(x * factor, y * factor, z * factor);
	}

	public TailsVec3d apply(DoubleUnaryOperator op) {
		return new TailsVec3d(op.applyAsDouble(x), op.applyAsDouble(y), op.applyAsDouble(z));
	}

	public TailsVec3d normalize() {
		final double len = length();

		if (len > 0) return scale(1 / len);

		return this;
	}

	public TailsVec3d lerp(TailsVec3d to, TailsVec3d delta) {
		return new TailsVec3d(
				TailsMath.lerp(delta.x, x, to.x),
				TailsMath.lerp(delta.y, y, to.y),
				TailsMath.lerp(delta.z, z, to.z));
	}

	public TailsVec3d lerp(TailsVec3d to, double delta) {
		return new TailsVec3d(
				TailsMath.lerp(delta, x, to.x),
				TailsMath.lerp(delta, y, to.y),
				TailsMath.lerp(delta, z, to.z));
	}

	public TailsVec3d rotateAroundAxis(TailsVec3d axis, double degrees) {
		final TailsVec3d normalizedAxis = axis.normalize();
		final Quaternionf vectorQuat = new Quaternionf((float) x, (float) y, (float) z, 0);
		final Quaternionf rotatorQuat = new Quaternionf().fromAxisAngleDeg((float) normalizedAxis.x, (float) normalizedAxis.y, (float) normalizedAxis.z, (float) degrees);
		final Quaternionf rotatorQuatConj = new Quaternionf(rotatorQuat);
		rotatorQuatConj.conjugate();

		rotatorQuat.mul(vectorQuat);
		rotatorQuat.mul(rotatorQuatConj);

		return new TailsVec3d(rotatorQuat.x(), rotatorQuat.y(), rotatorQuat.z());
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TailsVec3d)) return false;
		final TailsVec3d vec = (TailsVec3d) obj;
		return x == vec.x && y == vec.y && z == vec.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}