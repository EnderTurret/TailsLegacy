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
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartRegistry;

public class FakeEntityRenderHelper implements IRenderHelper<FakeEntity> {

	@Override
	public void onPreRenderTail(PoseStack poseStack, FakeEntity entity, PartRenderer tail, PartInfo info, MultiBufferSource bufferSource, VertexConsumer buffer, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		final Part part = info.getPart();
		switch (part.getType()) {
		case TAIL: {
			// Nine tails
			if (part == PartRegistry.FLUFFY_TAIL && info.getSubType() == 2)
				poseStack.translate(0F, 0.85F, 0F);
			else poseStack.translate(0F, 0.65F, 0F);
			poseStack.scale(0.9F, 0.9F, 0.9F);
			break;
		}
		case MUZZLE:
			poseStack.translate(0.2F, 1.25F, 0F);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
			poseStack.mulPose(Vector3f.YP.rotationDegrees(-45F));
			poseStack.mulPose(Vector3f.XP.rotationDegrees(25F));
			break;
		case EARS: {
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
			poseStack.translate(0F, 1.4F, 0F);
			break;
		}
		case WINGS: {
			poseStack.translate(0F, 0.9F, 0F);
			poseStack.scale(0.6F, 0.6F, 0.6F);
			break;
		}
		}
	}
}