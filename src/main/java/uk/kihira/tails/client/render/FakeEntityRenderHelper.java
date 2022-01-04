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
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartRegistry;

public class FakeEntityRenderHelper implements IRenderHelper<FakeEntity> {

	@Override
	public void onPreRenderTail(MatrixStack matrixStack, FakeEntity entity, PartRenderer tail, PartInfo info, IRenderTypeBuffer bufferIn, IVertexBuilder builderIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		final Part part = info.getPart();
		switch (part.getType()) {
		case TAIL: {
			// Nine tails
			if (part == PartRegistry.FLUFFY_TAIL && info.getSubType() == 2)
				matrixStack.translate(0F, 0.85F, 0F);
			else matrixStack.translate(0F, 0.65F, 0F);
			matrixStack.scale(0.9F, 0.9F, 0.9F);
			break;
		}
		// TODO fake head using players skin?
		case MUZZLE:
			matrixStack.translate(0.2F, 1.25F, 0F);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-45F));
			matrixStack.rotate(Vector3f.XP.rotationDegrees(25F));
			break;
		case EARS:
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
			matrixStack.translate(0F, 1.4F, 0F);
			break;
		case WINGS:
			matrixStack.translate(0F, 0.9F, 0F);
			matrixStack.scale(0.6F, 0.6F, 0.6F);
			break;
		case HEAD_ACCESSORY:
			if (part == PartRegistry.SEA_PICKLE)
				matrixStack.translate(0, 1.8F, 0);
			break;
		default:
			break;
		}
	}
}