/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.part;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import net.minecraft.util.Mth;

import uk.kihira.tails.common2.client.duck.TailsPoseStack;

public record Transformation(Vector3fc scale, Vector3fc offset, Vector3fc rotation) {

	public static final Vector3fc ZERO_VECTOR = new Vector3f();
	public static final Transformation ZERO = new Transformation(ZERO_VECTOR, ZERO_VECTOR, ZERO_VECTOR);

	public boolean isEmpty() {
		return this == ZERO || (scale.equals(ZERO_VECTOR) && offset.equals(ZERO_VECTOR) && rotation.equals(ZERO_VECTOR));
	}

	public void apply(TailsPoseStack pose) {
		if (!offset.equals(ZERO_VECTOR))
			pose.t$translate(offset.x(), offset.y(), offset.z());

		if (!rotation.equals(ZERO_VECTOR)) {
			pose.t$rotateX(rotation.x() * Mth.DEG_TO_RAD);
			pose.t$rotateY(rotation.y() * Mth.DEG_TO_RAD);
			pose.t$rotateZ(rotation.z() * Mth.DEG_TO_RAD);
		}

		if (!scale.equals(ZERO_VECTOR))
			pose.t$scale(scale.x(), scale.y(), scale.z());
	}
}