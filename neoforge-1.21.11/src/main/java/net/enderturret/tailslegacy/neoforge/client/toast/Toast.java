/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client.toast;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

public final class Toast {

	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;
	private final List<FormattedCharSequence> message;
	boolean mouseOver;
	int time;

	public Toast(int xPos, int yPos, int width, int time, FormattedCharSequence... message) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.time = time;
		this.message = Arrays.asList(message);
		height = this.message.size() * Minecraft.getInstance().font.lineHeight + 7;
	}

	public void drawToast(GuiGraphics gui, int mouseX, int mouseY) {
		if (time > 0) {
			mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
			int opacity = mouseOver ? 255 : (int) (time * 256F / 10F);
			if (opacity > 255) opacity = 255;
			if (mouseOver) time = 20;

			if (opacity > 0) {
				final Font font = Minecraft.getInstance().font;
				gui.pose().pushMatrix();
				drawBackdrop(gui, xPos, yPos, width, height);
				final int colour = 0xFFFFFF | opacity << 24;
				for (int i = 0; i < message.size(); i++) {
					final FormattedCharSequence s = message.get(i);
					gui.drawString(font, s, xPos + width / 2 - font.width(s) / 2, yPos + 4 + font.lineHeight * i, colour, true);
				}
				gui.pose().popMatrix();
			}
		}
	}

	private void drawBackdrop(GuiGraphics gui, int x, int y, int width, int height) {
		int opacity = mouseOver ? 255 : (int) (time * 256F / 25F);
		if (opacity > 255) opacity = 255;

		// Black back
		int colour = opacity << 24;
		gui.fill(x + 1, y, x + width - 1, y + height, colour);
		gui.fill(x, y + 1, x + 1, y + height - 1, colour);
		gui.fill(x + width - 1, y + 1, x + width, y + height - 1, colour);

		// Border
		colour = 0x28025c | opacity << 24;
		gui.fill(x + 1, y + 1, x + width - 1, y + 2, colour);
		gui.fill(x + 1, y + height - 1, x + width - 1, y + height - 2, colour);
		gui.fill(x + 1, y + 1, x + 2, y + height - 1, colour);
		gui.fill(x + width - 1, y + 1, x + width - 2, y + height - 1, colour);
	}
}
