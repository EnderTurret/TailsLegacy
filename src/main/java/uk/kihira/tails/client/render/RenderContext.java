/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.part.ClientPartInfo;

public record RenderContext(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
		float red, float green, float blue, float alpha, float partialTick,
		LivingEntity entity, ClientPartInfo info) {

	public void render(ModelPart part) {
		part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}