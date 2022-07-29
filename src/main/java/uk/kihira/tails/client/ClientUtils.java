/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.UUIDUtil;

public class ClientUtils {

	/**
	 * Draws a string that respects new lines.
	 * @param matrixStack The {@link PoseStack} to use for transformation information.
	 * @param fontRenderer The {@link Font} to use for drawing the text.
	 * @param string The text to draw.
	 * @param x The x position of the text.
	 * @param y The y position of the text.
	 * @param color The color of the text.
	 */
	public static void drawStringMultiLine(PoseStack matrixStack, Font fontRenderer, String string, int x, int y, int color) {
		final String[] lines = string.split("\n");
		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			fontRenderer.draw(matrixStack, line, x, y + fontRenderer.lineHeight * i, color);
		}
	}

	public static void drawCenteredString(PoseStack matrixStack, Font fontRenderer, String string, int x, int y, int color) {
		final int width = fontRenderer.width(string);
		fontRenderer.draw(matrixStack, string, x - width / 2, y, color);
	}

	public static UUID getPlayerUUID() {
		final Minecraft mc = Minecraft.getInstance();
		/*if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();*/
		return UUIDUtil.getOrCreatePlayerUUID(mc.getUser().getGameProfile());
	}
}