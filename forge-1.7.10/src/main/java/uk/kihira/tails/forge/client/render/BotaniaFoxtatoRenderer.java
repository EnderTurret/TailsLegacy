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
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import uk.kihira.tails.common.client.render.FoxtatoRenderer;
import uk.kihira.tails.forge.client.platform.TailsPoseStackImpl;
import uk.kihira.tails.forge.client.platform.TailsTessellatorWrapper;
import vazkii.botania.api.item.TinyPotatoRenderEvent;

/**
 * Handles rendering Tails accessories on tiny potatoes named "foxtato" (case-insensitive).
 */
@Internal
public final class BotaniaFoxtatoRenderer {

	private static FoxtatoRenderer renderer;

	public static void render(int x, int y, int z, float partialTick) {
		if (renderer == null) renderer = new FoxtatoRenderer();

		renderer.render(TailsPoseStackImpl.INSTANCE, TailsTessellatorWrapper.get(), x, y, z, partialTick, 1, 1);

		GL11.glColor4f(1F, 1F, 1F, 1F);
	}

	@SubscribeEvent
	public void onPotatoRender(TinyPotatoRenderEvent e) {
		if (e.name.equalsIgnoreCase("foxtato"))
			render(e.tile.xCoord, e.tile.yCoord, e.tile.zCoord, e.partTicks);
	}
}