/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.regex.Pattern;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;

public class ClientUtils {

	private static final Pattern NEWLINE_SPLITTER = Pattern.compile("\n");

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
		final String[] lines = NEWLINE_SPLITTER.split(string);
		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			fontRenderer.drawString(matrixStack, line, x, y + fontRenderer.FONT_HEIGHT * i, color);
		}
	}

	public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontRenderer, String string, int x, int y, int color) {
		final int width = fontRenderer.getStringWidth(string);
		fontRenderer.drawString(matrixStack, string, x - width / 2, y, color);
	}
}