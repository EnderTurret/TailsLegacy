/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.util.Mth;

public record Transformation(Vector3fc scale, Vector3fc offset, Vector3fc rotation) {

	public static final Vector3fc ZERO_VECTOR = new Vector3f();
	public static final Transformation ZERO = new Transformation(ZERO_VECTOR, ZERO_VECTOR, ZERO_VECTOR);

	public boolean isEmpty() {
		return this == ZERO || (scale.equals(ZERO_VECTOR) && offset.equals(ZERO_VECTOR) && rotation.equals(ZERO_VECTOR));
	}

	public void apply(PoseStack pose) {
		if (!offset.equals(ZERO_VECTOR))
			pose.translate(offset.x(), offset.y(), offset.z());

		if (!rotation.equals(ZERO_VECTOR)) {
			final Quaternionf rot = new Quaternionf()
					.rotateX(rotation.x() * Mth.DEG_TO_RAD)
					.rotateY(rotation.y() * Mth.DEG_TO_RAD)
					.rotateZ(rotation.z() * Mth.DEG_TO_RAD);
			pose.mulPose(rot);
		}

		if (!scale.equals(ZERO_VECTOR))
			pose.scale(scale.x(), scale.y(), scale.z());
	}
}