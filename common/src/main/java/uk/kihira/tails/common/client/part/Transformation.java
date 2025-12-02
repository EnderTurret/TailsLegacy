/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.part;

import java.util.Objects;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.TailsVec3f;
import uk.kihira.tails.common.client.duck.TailsPoseStack;

public final class Transformation {

	public static final Transformation ZERO = new Transformation(TailsVec3f.ZERO, TailsVec3f.ZERO, TailsVec3f.ZERO);

	private final TailsVec3f scale;
	private final TailsVec3f offset;
	private final TailsVec3f rotation;

	public Transformation(TailsVec3f scale, TailsVec3f offset, TailsVec3f rotation) {
		this.scale = scale;
		this.offset = offset;
		this.rotation = rotation;
	}

	public TailsVec3f scale() {
		return scale;
	}

	public TailsVec3f offset() {
		return offset;
	}

	public TailsVec3f rotation() {
		return rotation;
	}

	public boolean isEmpty() {
		return this == ZERO || (scale.equals(TailsVec3f.ZERO) && offset.equals(TailsVec3f.ZERO) && rotation.equals(TailsVec3f.ZERO));
	}

	public void apply(TailsPoseStack pose) {
		if (!offset.equals(TailsVec3f.ZERO))
			pose.t$translate(offset.x(), offset.y(), offset.z());

		if (!rotation.equals(TailsVec3f.ZERO)) {
			pose.t$rotateX(rotation.x() * TailsMath.DEG_TO_RAD);
			pose.t$rotateY(rotation.y() * TailsMath.DEG_TO_RAD);
			pose.t$rotateZ(rotation.z() * TailsMath.DEG_TO_RAD);
		}

		if (!scale.equals(TailsVec3f.ZERO))
			pose.t$scale(scale.x(), scale.y(), scale.z());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Transformation)) return false;
		final Transformation t = (Transformation) obj;
		return offset.equals(t.offset) && rotation.equals(t.rotation) && scale.equals(t.scale);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scale, offset, rotation);
	}
}