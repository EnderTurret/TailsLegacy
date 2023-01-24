/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.helper;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.math.Vector3f;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.FoxtatoRenderer;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * An {@link IRenderHelper} for "fake" entities -- that is, the part preview and/or {@linkplain FoxtatoRenderer foxtato}.
 */
@Internal
public final class FakeEntityRenderHelper implements IRenderHelper<FakeEntity> {

	@Override
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer) {
		final Part part = ctx.info().getPart();
		switch (part.getAttachment().id()) {
		case "body/tail" -> {
			ctx.poseStack().translate(0, 0.65, 0);
			ctx.poseStack().scale(0.9F, 0.9F, 0.9F);
		}
		case "head/face" -> {
			ctx.poseStack().translate(0.2, 1.25, 0);
			ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(180F));
			ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(-45F));
			ctx.poseStack().mulPose(Vector3f.XP.rotationDegrees(25F));
		}
		case "head/top_ears" -> {
			ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(180F));
			ctx.poseStack().translate(0, 1.4, 0);
		}
		case "body/back" -> {
			ctx.poseStack().translate(0, 0.9, 0);
			ctx.poseStack().scale(0.6F, 0.6F, 0.6F);
		}
		}

		if (renderer.modelPart != null)
			renderer.modelPart.setupPartPreviewAnim(ctx, renderer);
	}
}