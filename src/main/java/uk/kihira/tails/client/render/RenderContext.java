/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.ClientPartsData;

/**
 * Contains all the context necessary for rendering parts.
 * Created to consolidate the hundreds of parameters being accumulated in the render methods.
 * @param poseStack The {@link PoseStack} to use for transformations.
 * @param buffer The buffer to render to.
 * @param packedLight The packed light.
 * @param packedOverlay The packed overlay.
 * @param red The red color.
 * @param green The green color.
 * @param blue The blue color.
 * @param alpha The transparency.
 * @param partialTick The partial tick.
 * @param entity The entity being rendered.
 * @param parts The entity's full part data.
 * @param info The part data.
 * @author EnderTurret
 */
public record RenderContext(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
		float red, float green, float blue, float alpha, float partialTick,
		LivingEntity entity, @Nullable ClientPartsData parts, ClientPartInfo info) {

	/**
	 * Calls {@link ModelPart#render(PoseStack, VertexConsumer, int, int, float, float, float, float)} on the given part with parameters from this {@link RenderContext}.
	 * @param part The part to render.
	 * @param packedLight
	 * @param packedOverlay
	 */
	public void render(ModelPart part, int packedLight, int packedOverlay) {
		part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * Calls {@link ModelPart#render(PoseStack, VertexConsumer, int, int, float, float, float, float)} on the given part with parameters from this {@link RenderContext}.
	 * @param part The part to render.
	 */
	public void render(ModelPart part) {
		render(part, packedLight, packedOverlay);
	}
}