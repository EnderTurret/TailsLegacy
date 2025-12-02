/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.render.helper;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common.client.api.IRenderHelper;
import uk.kihira.tails.common.client.part.PartRegistry;
import uk.kihira.tails.common.client.render.RenderContext;
import uk.kihira.tails.common.client.render.part.PartRenderer;

/**
 * An {@link IRenderHelper} for players.
 */
@Internal
public final class PlayerRenderHelper implements IRenderHelper {

	@Internal
	public PlayerRenderHelper() {}

	@Override
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer) {
		if (!ctx.entity().t$isPlayer() || !"body/tail".equals(ctx.info().getPart().getAttachment().id())) return;

		final boolean crouching = ctx.entity().t$isCrouching();

		if (ctx.info().getPartId().equals(PartRegistry.DRAGON_TAIL.id())) {
			if (crouching) {
				ctx.poseStack().t$translate(0F, 0.55F, 0F);
				ctx.poseStack().t$rotateX(0.4F);
			}
			else ctx.poseStack().t$translate(0F, 0.68F, 0.1F);
			ctx.poseStack().t$scale(0.8F, 0.8F, 0.8F);
		}

		else if (ctx.info().getPartId().equals(PartRegistry.CAT_TAIL.id()) || ctx.info().getPartId().equals(PartRegistry.DEVIL_TAIL.id())) {
			ctx.poseStack().t$translate(0F, 0.65F, 0.1F);
			if (crouching)
				ctx.poseStack().t$rotateX(0.4F);
			ctx.poseStack().t$scale(0.9F, 0.9F, 0.9F);
		}

		else {
			ctx.poseStack().t$translate(0F, 0.65F, 0.1F);
			ctx.poseStack().t$scale(0.8F, 0.8F, 0.8F);
		}
	}
}