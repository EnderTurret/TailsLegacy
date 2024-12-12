/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.helper;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.part.PartRegistry;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * An {@link IRenderHelper} for players.
 * @param <T> The type.
 */
@Internal
public final class PlayerRenderHelper<T extends LivingEntity> implements IRenderHelper<T> {

	@Internal
	public PlayerRenderHelper() {}

	@Override
	public void onPreRenderTail(RenderContext ctx, PartRenderer renderer) {
		if (!"body/tail".equals(ctx.info().getPart().getAttachment().id())) return;

		if (ctx.info().getPartId().equals(PartRegistry.DRAGON_TAIL.id())) {
			if (ctx.entity().isShiftKeyDown()) {
				ctx.poseStack().translate(0F, 0.55F, 0F);
				ctx.poseStack().mulPose(new Matrix4f().rotateX(0.4F));
			}
			else ctx.poseStack().translate(0F, 0.68F, 0.1F);
			ctx.poseStack().scale(0.8F, 0.8F, 0.8F);
		}
		else if (ctx.info().getPartId().equals(PartRegistry.CAT_TAIL.id()) || ctx.info().getPartId().equals(PartRegistry.DEVIL_TAIL.id())) {
			ctx.poseStack().translate(0F, 0.65F, 0.1F);
			if (ctx.entity().isShiftKeyDown())
				ctx.poseStack().mulPose(new Matrix4f().rotateX(0.4F));
			ctx.poseStack().scale(0.9F, 0.9F, 0.9F);
		}
		else {
			ctx.poseStack().translate(0F, 0.65F, 0.1F);
			ctx.poseStack().scale(0.8F, 0.8F, 0.8F);
		}
	}
}