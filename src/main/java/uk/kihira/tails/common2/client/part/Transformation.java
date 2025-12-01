/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.part;

import uk.kihira.tails.common2.TailsMath;
import uk.kihira.tails.common2.client.TailsVec3f;
import uk.kihira.tails.common2.client.duck.TailsPoseStack;

public record Transformation(TailsVec3f scale, TailsVec3f offset, TailsVec3f rotation) {

	public static final Transformation ZERO = new Transformation(TailsVec3f.ZERO, TailsVec3f.ZERO, TailsVec3f.ZERO);

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
}