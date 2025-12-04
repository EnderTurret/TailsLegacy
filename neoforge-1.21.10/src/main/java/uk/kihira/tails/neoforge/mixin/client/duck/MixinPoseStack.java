/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.mixin.client.duck;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import uk.kihira.tails.common.client.duck.TailsPoseStack;

@Mixin(PoseStack.class)
public class MixinPoseStack implements TailsPoseStack {

	@Override
	public void t$push() {
		((PoseStack) (Object) this).pushPose();
	}

	@Override
	public void t$pop() {
		((PoseStack) (Object) this).popPose();
	}

	@Override
	public void t$translate(double x, double y, double z) {
		((PoseStack) (Object) this).translate(x, y, z);
	}

	@Override
	public void t$translate(float x, float y, float z) {
		((PoseStack) (Object) this).translate(x, y, z);
	}

	@Override
	public void t$rotateX(float radians) {
		((PoseStack) (Object) this).mulPose(new Quaternionf().rotateX(radians));
	}

	@Override
	public void t$rotateY(float radians) {
		((PoseStack) (Object) this).mulPose(new Quaternionf().rotateY(radians));
	}

	@Override
	public void t$rotateZ(float radians) {
		((PoseStack) (Object) this).mulPose(new Quaternionf().rotateZ(radians));
	}

	@Override
	public void t$scale(float x, float y, float z) {
		((PoseStack) (Object) this).scale(x, y, z);
	}
}