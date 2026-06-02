/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.render.helper;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.api.IRenderHelper;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.render.FoxtatoRenderer;
import net.enderturret.tailslegacy.common.client.render.RenderContext;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;

/**
 * An {@link IRenderHelper} for "fake" entities -- that is, the part preview and/or {@linkplain FoxtatoRenderer foxtato}.
 */
@Internal
public final class FakeEntityRenderHelper implements IRenderHelper {

	@Override
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer) {
		if (!ctx.entity().t$isPreview()) return;

		final Part part = ctx.info().getPart();
		switch (part.getAttachment().id()) {
			case "body/tail":
				ctx.poseStack().t$translate(0, 0.65, 0);
				ctx.poseStack().t$scale(0.9F, 0.9F, 0.9F);
				break;
			case "body/back":
				ctx.poseStack().t$translate(0, 0.9, 0);
				ctx.poseStack().t$scale(0.6F, 0.6F, 0.6F);
				break;
			case "head/face":
				ctx.poseStack().t$translate(0.2, 1.25, 0);
				ctx.poseStack().t$rotateY(TailsMath.PI);
				ctx.poseStack().t$rotateY(-45F * TailsMath.DEG_TO_RAD);
				ctx.poseStack().t$rotateX(25F * TailsMath.DEG_TO_RAD);
				break;
			default:
				if ("head".equals(part.getAttachment().root().id())) {
					ctx.poseStack().t$rotateY(TailsMath.PI);
					ctx.poseStack().t$translate(0, 1.4, 0);
				}
		}

		if (renderer.modelPart != null)
			renderer.modelPart.setupPartPreviewAnim(ctx, renderer);
	}
}