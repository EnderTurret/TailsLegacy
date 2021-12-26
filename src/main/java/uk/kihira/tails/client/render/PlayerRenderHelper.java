/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.model.tail.CatTailModel;
import uk.kihira.tails.client.model.tail.DevilTailModel;
import uk.kihira.tails.client.model.tail.DragonTailModel;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;

public class PlayerRenderHelper implements IRenderHelper<PlayerEntity> {

	//private final boolean mpmCompat;

	public PlayerRenderHelper() {
		//mpmCompat = ModList.get().isLoaded("moreplayermodels");
	}

	@Override
	public void onPreRenderTail(MatrixStack matrixStack, PlayerEntity entity, PartRenderer tail, PartInfo info, IRenderTypeBuffer bufferIn, IVertexBuilder builderIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (info.getPart().getType() != PartType.TAIL) return;
		//if (mpmCompat && entity.isSneaking())
		//matrixStack.translate(0f, -0.1f, 0.4f);
		if (tail.modelPart instanceof DragonTailModel) {
			if (entity.isShiftKeyDown()) matrixStack.translate(0f, 0.82f, 0f);
			else matrixStack.translate(0F, 0.68F, 0.1F);
			matrixStack.scale(0.8F, 0.8F, 0.8F);
		}
		else if (tail.modelPart instanceof CatTailModel || tail.modelPart instanceof DevilTailModel) {
			if (entity.isShiftKeyDown()) matrixStack.translate(0f, 0.82f, 0f);
			else matrixStack.translate(0F, 0.65F, 0.1F);
			matrixStack.scale(0.9F, 0.9F, 0.9F);
		}
		else {
			matrixStack.translate(0F, 0.65F, 0.1F);
			matrixStack.scale(0.8F, 0.8F, 0.8F);
		}
	}
}