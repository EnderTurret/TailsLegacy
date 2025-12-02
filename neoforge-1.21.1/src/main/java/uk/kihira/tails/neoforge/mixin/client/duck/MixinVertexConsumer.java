package uk.kihira.tails.neoforge.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsPoseStack;

@Mixin(VertexConsumer.class)
public interface MixinVertexConsumer extends TailsBuffer {

	@Override
	public default TailsBuffer t$beginVertex(TailsPoseStack pose, float x, float y, float z) {
		((VertexConsumer) this).addVertex(((PoseStack) pose).last(), x, y, z);
		return this;
	}

	@Override
	public default TailsBuffer t$color(int color) {
		((VertexConsumer) this).setColor(color);
		return this;
	}

	@Override
	public default TailsBuffer t$uv(float u, float v) {
		((VertexConsumer) this).setUv(u, v);
		return this;
	}

	@Override
	public default TailsBuffer t$overlay(int overlay) {
		((VertexConsumer) this).setOverlay(overlay);
		return this;
	}

	@Override
	public default TailsBuffer t$light(int light) {
		((VertexConsumer) this).setLight(light);
		return this;
	}

	@Override
	public default TailsBuffer t$normal(TailsPoseStack pose, float x, float y, float z) {
		((VertexConsumer) this).setNormal(((PoseStack) pose).last(), x, y, z);
		return this;
	}

	@Override
	public default TailsBuffer t$endVertex() {
		return this;
	}
}