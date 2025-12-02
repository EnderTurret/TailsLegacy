package uk.kihira.tails.common2.client;

import java.util.Objects;

public final class TailsVec3f {

	private final float x;
	private final float y;
	private final float z;

	public static final TailsVec3f ZERO = new TailsVec3f(0, 0, 0);

	public TailsVec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	public float z() {
		return z;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TailsVec3f)) return false;
		final TailsVec3f vec = (TailsVec3f) obj;
		return x == vec.x && y == vec.y && z == vec.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}