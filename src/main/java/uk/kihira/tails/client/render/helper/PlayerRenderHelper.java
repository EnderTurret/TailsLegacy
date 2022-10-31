/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.helper;

import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.model.tail.CatTailModel;
import uk.kihira.tails.client.model.tail.DevilTailModel;
import uk.kihira.tails.client.model.tail.DragonTailModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.part.PartType;

public final class PlayerRenderHelper implements IRenderHelper<Player> {

	//private final boolean mpmCompat;

	public PlayerRenderHelper() {
		//mpmCompat = ModList.get().isLoaded("moreplayermodels");
	}

	@Override
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer) {
		if (ctx.info().getPart().getType() != PartType.TAIL) return;
		//if (mpmCompat && entity.isSneaking())
		//poseStack.translate(0f, -0.1f, 0.4f);
		if (renderer.modelPart instanceof DragonTailModel) {
			if (ctx.entity().isShiftKeyDown()) ctx.poseStack().translate(0f, 0.82f, 0f);
			else ctx.poseStack().translate(0F, 0.68F, 0.1F);
			ctx.poseStack().scale(0.8F, 0.8F, 0.8F);
		}
		else if (renderer.modelPart instanceof CatTailModel || renderer.modelPart instanceof DevilTailModel) {
			if (ctx.entity().isShiftKeyDown()) ctx.poseStack().translate(0f, 0.82f, 0f);
			else ctx.poseStack().translate(0F, 0.65F, 0.1F);
			ctx.poseStack().scale(0.9F, 0.9F, 0.9F);
		}
		else {
			ctx.poseStack().translate(0F, 0.65F, 0.1F);
			ctx.poseStack().scale(0.8F, 0.8F, 0.8F);
		}
	}
}