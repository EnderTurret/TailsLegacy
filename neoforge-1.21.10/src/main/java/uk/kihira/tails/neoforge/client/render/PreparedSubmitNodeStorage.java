package uk.kihira.tails.neoforge.client.render;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeStorage;

import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsPoseStack.Entry;
import uk.kihira.tails.common.client.duck.TailsVertexConsumer;

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
		storage.submitModelPart((ModelPart) (Object) part, (PoseStack) poseStack, type, packedLight, packedOverlay, null, color, null);
	}
}