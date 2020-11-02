/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.common.part.PartInfo;

/**
 * A pre-render callback for part rendering.<br>
 * Called just before rendering in {@link PartRenderer#preRender(MatrixStack, LivingEntity, PartInfo, double, double, double, float)}.
 */
public interface IRenderHelper {

	/**
	 * Handles pre-render transformations and other fun stuff.<br><br>
	 * You could render a sea pickle above the player's head here, albeit needing the buffers from everyone's favorite singleton: {@link Minecraft}.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the parts are being rendered on.
	 * @param tail The part renderer.
	 * @param info The part being rendered.
	 * @param x The x position.
	 * @param y The y position.
	 * @param z The z position.
	 */
	public void onPreRenderTail(MatrixStack matrixStack, LivingEntity entity, PartRenderer tail, PartInfo info, double x, double y, double z);
}