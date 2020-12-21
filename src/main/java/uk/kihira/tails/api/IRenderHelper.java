/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.common.part.PartInfo;

/**
 * A pre-render callback for part rendering.<br>
 * Called just before rendering in {@link PartRenderer#preRender(MatrixStack, LivingEntity, PartInfo, IRenderTypeBuffer, IVertexBuilder, double, double, double, float, int, int, float, float, float, float)}.
 *
 * @param <T> The type of entity this helper is for.
 */
@FunctionalInterface // I don't know why someone would willingly make a lambda with *this* many arguments, but sure, why not.
public interface IRenderHelper<T extends LivingEntity> {

	/**
	 * Handles pre-render transformations and other fun stuff.<br><br>
	 * You could render a sea pickle above the player's head here, if you wanted to.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the parts are being rendered on.
	 * @param tail The part renderer.
	 * @param info The part being rendered.
	 * @param bufferIn The buffers to use for getting new buffers.
	 * @param builderIn The builder for rendering to.
	 * @param x The x position.
	 * @param y The y position.
	 * @param z The z position.
	 * @param partialTicks The partial ticks.
	 * @param packedLightIn The packed light value.
	 * @param packedOverlayIn The packed overlay value.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @param alpha The transparency value.
	 */
	public void onPreRenderTail(MatrixStack matrixStack, T entity, PartRenderer tail, PartInfo info, IRenderTypeBuffer bufferIn, IVertexBuilder builderIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
}