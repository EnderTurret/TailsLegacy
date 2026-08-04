/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;

import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsVertexConsumer;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack.Entry;

public record PreparedSubmitNodeStorage(SubmitNodeStorage storage, RenderType type) implements TailsBuffer {

	@Override
	public void t$submitCustomGeometry(TailsPoseStack poseStack, BiConsumer<Entry, TailsVertexConsumer> renderer) {
		storage.submitCustomGeometry(
				(PoseStack) poseStack,
				type,
				(pose, consumer) -> renderer.accept((TailsPoseStack.Entry) (Object) pose, (TailsVertexConsumer) consumer));
	}

	@Override
	public void t$submitModelPart(TailsModelPart part, TailsPoseStack poseStack, int packedLight, int packedOverlay, int color) {
		final ModelPartRenderData data = new ModelPartRenderData();
		data.part = (ModelPart) (Object) part;
		data.packedLight = packedLight;
		data.packedOverlay = packedOverlay;
		data.color = color;

		for (ModelPart p : data.part.getAllParts())
			data.partStates.add(new ModelPartTransforms(p, new PartPose(p.x, p.y, p.z, p.xRot, p.yRot, p.zRot, p.xScale, p.yScale, p.zScale), p.visible));

		storage.submitCustomGeometry((PoseStack) poseStack, type, (pose, consumer) -> {
			final PoseStack stack = new PoseStack();
			stack.last().mulPose(pose.pose());

			for (ModelPartTransforms trans : data.partStates) {
				trans.part.visible = trans.visible;
				trans.part.loadPose(trans.pose);
			}

			data.part.render(stack, consumer, packedLight, packedOverlay);
		});
	}

	private static final class ModelPartRenderData {

		public ModelPart part;
		public int packedLight;
		public int packedOverlay;
		public int color;

		public final List<ModelPartTransforms> partStates = new ArrayList<>();
	}

	private static record ModelPartTransforms(ModelPart part, PartPose pose, boolean visible) {}
}