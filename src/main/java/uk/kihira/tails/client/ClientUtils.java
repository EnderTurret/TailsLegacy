/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class ClientUtils {

	/**
	 * Draws a string that respects new lines.
	 * @param matrixStack The {@link MatrixStack} to use for transformation information.
	 * @param fontRenderer The {@link FontRenderer} to use for drawing the text.
	 * @param string The text to draw.
	 * @param x The x position of the text.
	 * @param y The y position of the text.
	 * @param color The color of the text.
	 */
	public static void drawStringMultiLine(MatrixStack matrixStack, FontRenderer fontRenderer, String string, int x, int y, int color) {
		final String[] lines = string.split("\n");
		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			fontRenderer.drawString(matrixStack, line, x, y + fontRenderer.FONT_HEIGHT * i, color);
		}
	}

	public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontRenderer, String string, int x, int y, int color) {
		final int width = fontRenderer.getStringWidth(string);
		fontRenderer.drawString(matrixStack, string, x - width / 2, y, color);
	}

	public static UUID getPlayerUUID() {
		final Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();
		return PlayerEntity.getUUID(mc.getSession().getProfile());
	}
}