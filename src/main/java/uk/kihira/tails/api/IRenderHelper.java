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

public interface IRenderHelper {
	public void onPreRenderTail(MatrixStack matrixStack, LivingEntity entity, PartRenderer tail, PartInfo info, double x, double y, double z);
}