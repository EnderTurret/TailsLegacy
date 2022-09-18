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
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.part.PartRegistry;

public final class FakeEntityRenderHelper implements IRenderHelper<FakeEntity> {

	@Override
	public void onPreRenderTail(PoseStack poseStack, FakeEntity entity, PartRenderer tail, ClientPartInfo info, MultiBufferSource bufferSource, VertexConsumer buffer, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		final Part part = info.getPart();
		switch (part.getType()) {
		case TAIL -> {
			// Nine tails
			if (part == PartRegistry.FLUFFY_TAIL.get() && info.getSubType().id().equals("nine_tails"))
				poseStack.translate(0, 0.85, 0);
			else poseStack.translate(0, 0.65, 0);
			poseStack.scale(0.9F, 0.9F, 0.9F);
		}
		case MUZZLE -> {
			poseStack.translate(0.2, 1.25, 0);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
			poseStack.mulPose(Vector3f.YP.rotationDegrees(-45F));
			poseStack.mulPose(Vector3f.XP.rotationDegrees(25F));
		}
		case EARS -> {
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
			poseStack.translate(0, 1.4, 0);
		}
		case WINGS -> {
			poseStack.translate(0, 0.9, 0);
			poseStack.scale(0.6F, 0.6F, 0.6F);
		}
		}
	}
}