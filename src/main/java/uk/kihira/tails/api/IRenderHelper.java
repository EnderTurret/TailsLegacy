/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import uk.kihira.tails.client.render.RenderPart;
import uk.kihira.tails.common.PartInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;

public interface IRenderHelper {
	void onPreRenderTail(MatrixStack matrixStack, LivingEntity entity, RenderPart tail, PartInfo info, double x, double y, double z);
}
