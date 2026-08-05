/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.render;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.render.FoxtatoRenderer;

/**
 * Handles rendering Tails accessories on tiny potatoes named "foxtato" (case-insensitive).
 */
@Internal
public final class BotaniaFoxtatoRenderer {

	private static FoxtatoRenderer renderer;

	public static void render(PoseStack poseStack, MultiBufferSource buffers, BlockPos pos, float partialTicks, int packedLight, int packedOverlay) {
		if (renderer == null) renderer = new FoxtatoRenderer();

		renderer.render((TailsPoseStack) poseStack, (TailsBufferSource) buffers, pos.getX(), pos.getY(), pos.getZ(), partialTicks, packedLight, packedOverlay);
	}

	public static void register() {

	}

	/*
	@SubscribeEvent
	public static void onPotatoRender(TinyPotatoRenderEvent e) {
		if (e.name.getString().equalsIgnoreCase("foxtato"))
			render(e.ms, e.buffers, e.tile.getBlockPos(), e.partTicks, e.light, e.overlay);
	}
	*/
}