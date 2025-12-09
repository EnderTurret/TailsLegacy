/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.render.FoxtatoRenderer;
import vazkii.botania.api.item.TinyPotatoRenderEvent;

/**
 * Handles rendering Tails accessories on tiny potatoes named "foxtato" (case-insensitive).
 */
@Internal
public final class BotaniaFoxtatoRenderer {

	private static FoxtatoRenderer renderer;

	public static void render(BlockPos pos, float partialTick) {
		if (renderer == null) renderer = new FoxtatoRenderer();

		renderer.render((TailsPoseStack) poseStack, (TailsBufferSource) buffers, pos.getX(), pos.getY(), pos.getZ(), partialTick, 1, 1);

		GlStateManager.color(1F, 1F, 1F, 1F);
	}

	@SubscribeEvent
	public static void onPotatoRender(TinyPotatoRenderEvent e) {
		if (e.name.equalsIgnoreCase("foxtato"))
			render(e.tile.getPos(), e.partTicks);
	}
}