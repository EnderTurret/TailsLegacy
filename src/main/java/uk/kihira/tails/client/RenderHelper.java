/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 *
 * Some code provided by iChun under LGPLv3
 */

package uk.kihira.tails.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;

public class RenderHelper {

	public static void startGlScissor(int x, int y, int width, int height) {
		final Window mc = Minecraft.getInstance().getWindow();

		final double scaleW = (double)mc.getScreenWidth() / mc.getGuiScaledWidth();
		final double scaleH = (double)mc.getScreenHeight() / mc.getGuiScaledHeight();

		RenderSystem.enableScissor((int)Math.floor(x * scaleW), (int)Math.floor(mc.getScreenHeight() - (y + height) * scaleH), (int)Math.floor((x + width) * scaleW) - (int)Math.floor(x * scaleW), (int)Math.floor(mc.getScreenHeight() - y * scaleH) - (int)Math.floor(mc.getScreenHeight() - (y + height) * scaleH)); // Starts from lower left corner (minecraft starts from upper left)
	}

	public static void endGlScissor() {
		RenderSystem.disableScissor();
	}
}