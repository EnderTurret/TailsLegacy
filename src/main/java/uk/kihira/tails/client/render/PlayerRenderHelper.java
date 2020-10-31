/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.model.tail.ModelCatTail;
import uk.kihira.tails.client.model.tail.ModelDevilTail;
import uk.kihira.tails.client.model.tail.ModelDragonTail;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;

public class PlayerRenderHelper implements IRenderHelper {

	private boolean mpmCompat;

	public PlayerRenderHelper() {
		mpmCompat = ModList.get().isLoaded("moreplayermodels");
	}

	@Override
	public void onPreRenderTail(MatrixStack matrixStack, LivingEntity entity, RenderPart tail, PartInfo info, double x, double y, double z) {
		if (info.partType == PartsData.PartType.EARS || info.partType == PartsData.PartType.MUZZLE || info.partType == PartsData.PartType.WINGS) return;
		if (mpmCompat && entity.isSneaking())
			matrixStack.translate(0f, -0.1f, 0.4f);
		if (tail.modelPart instanceof ModelDragonTail) {
			if (entity.isSneaking()) matrixStack.translate(0f, 0.82f, 0f);
			else matrixStack.translate(0F, 0.68F, 0.1F);
			matrixStack.scale(0.8F, 0.8F, 0.8F);
		}
		else if (tail.modelPart instanceof ModelCatTail || tail.modelPart instanceof ModelDevilTail) {
			if (entity.isSneaking()) matrixStack.translate(0f, 0.82f, 0f);
			else matrixStack.translate(0F, 0.65F, 0.1F);
			matrixStack.scale(0.9F, 0.9F, 0.9F);
		}
		else {
			if (entity.isSneaking()) matrixStack.translate(0f, 0.82f, 0f);
			else matrixStack.translate(0F, 0.65F, 0.1F);
			matrixStack.scale(0.8F, 0.8F, 0.8F);
		}
	}
}
