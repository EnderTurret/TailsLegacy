/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.client.duck;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;

import uk.kihira.tails.common.JavaColor;
import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsVertexConsumer;

@Mixin(VertexConsumer.class)
public interface MixinVertexConsumer extends TailsBuffer, TailsVertexConsumer {

	@Override
	public default void t$submitCustomGeometry(TailsPoseStack poseStack, BiConsumer<TailsPoseStack.Entry, TailsVertexConsumer> renderer) {
		renderer.accept(poseStack.t$lastEntry(), this);
	}

	@Override
	public default void t$submitModelPart(TailsModelPart part, TailsPoseStack poseStack, int packedLight, int packedOverlay, int color) {
		((ModelPart) (Object) part).render((PoseStack) poseStack, (VertexConsumer) this, packedLight, packedOverlay,
				JavaColor.red(color) / 255F, JavaColor.green(color) / 255F, JavaColor.blue(color) / 255F, JavaColor.alpha(color) / 255F);
	}

	@Override
	public default TailsVertexConsumer t$beginVertex(TailsPoseStack.Entry pose, float x, float y, float z) {
		((VertexConsumer) this).vertex(((PoseStack.Pose) (Object) pose).pose(), x, y, z);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$color(int color) {
		((VertexConsumer) this).color(color);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$uv(float u, float v) {
		((VertexConsumer) this).uv(u, v);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$overlay(int overlay) {
		((VertexConsumer) this).overlayCoords(overlay);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$light(int light) {
		((VertexConsumer) this).uv2(light);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$normal(float x, float y, float z) {
		((VertexConsumer) this).normal(x, y, z);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$normal(TailsPoseStack.Entry pose, float x, float y, float z) {
		((VertexConsumer) this).normal(((PoseStack.Pose) (Object) pose).normal(), x, y, z);
		return this;
	}

	@Override
	public default TailsVertexConsumer t$endVertex() {
		((VertexConsumer) this).endVertex();
		return this;
	}
}