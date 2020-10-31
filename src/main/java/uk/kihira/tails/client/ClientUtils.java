package uk.kihira.tails.client;

import java.util.regex.Pattern;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;

public class ClientUtils {

	private static final Pattern NEWLINE_SPLITTER = Pattern.compile("\n");

    /**
     * Draws a string that respects new lines
     * @param fontRenderer Font Renderer
     * @param string Text
     * @param x X Position
     * @param y Y Position
     * @param color Text Colour
     */
    public static void drawStringMultiLine(MatrixStack matrixStack, FontRenderer fontRenderer, String string, int x, int y, int color) {
        String[] lines = NEWLINE_SPLITTER.split(string);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            fontRenderer.drawString(matrixStack, line, x, y + (fontRenderer.FONT_HEIGHT * i), color);
        }
    }

    public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontRenderer, String string, int x, int y, int color) {
        int width = fontRenderer.getStringWidth(string);
        fontRenderer.drawString(matrixStack, string, x - width / 2, y, color);
    }
}
