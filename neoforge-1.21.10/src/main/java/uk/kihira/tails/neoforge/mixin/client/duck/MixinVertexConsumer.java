/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsVertexConsumer;

@Mixin(VertexConsumer.class)
public interface MixinVertexConsumer extends TailsVertexConsumer {

	@Override
	public default TailsVertexConsumer t$beginVertex(TailsPoseStack.Entry pose, float x, float y, float z) {
		((VertexConsumer) this).addVertex((PoseStack.Pose) (Object) pose, x, y, z);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$color(int color) {
		((VertexConsumer) this).setColor(color);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$uv(float u, float v) {
		((VertexConsumer) this).setUv(u, v);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$overlay(int overlay) {
		((VertexConsumer) this).setOverlay(overlay);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$light(int light) {
		((VertexConsumer) this).setLight(light);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$normal(TailsPoseStack.Entry pose, float x, float y, float z) {
		((VertexConsumer) this).setNormal((PoseStack.Pose) (Object) pose, x, y, z);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$endVertex() {
		return this;
	}
}