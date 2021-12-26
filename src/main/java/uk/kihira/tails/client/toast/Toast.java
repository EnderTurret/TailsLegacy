/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.toast;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;

public class Toast {

	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;
	private final List<IReorderingProcessor> message;
	boolean mouseOver;
	int time;

	public Toast(int xPos, int yPos, int width, int time, IReorderingProcessor... message) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.time = time;
		this.message = Arrays.asList(message);
		height = this.message.size() * Minecraft.getInstance().font.lineHeight + 7;
	}

	public void drawToast(MatrixStack matrixStack, int mouseX, int mouseY) {
		if (time > 0) {
			final FontRenderer fontRenderer = Minecraft.getInstance().font;
			mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
			int opacity = mouseOver ? 255 : (int) (time * 256F / 10F);
			if (opacity > 255) opacity = 255;
			if (mouseOver) time = 20;

			if (opacity > 0) {
				matrixStack.pushPose();
				RenderSystem.enableBlend();
				RenderSystem.disableLighting();
				RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				drawBackdrop(matrixStack, xPos, yPos, width, height);
				final int colour = 0xFFFFFF | opacity << 24;
				for (int i = 0; i < message.size(); i++) {
					final IReorderingProcessor s = message.get(i);
					fontRenderer.drawShadow(matrixStack, s, xPos + width / 2 - fontRenderer.width(s) / 2, yPos + 4 + fontRenderer.lineHeight * i, colour);
				}
				RenderSystem.disableBlend();
				RenderSystem.color4f(0F, 0F, 0F, 1F);
				matrixStack.popPose();
			}
		}
	}

	private void drawBackdrop(MatrixStack matrixStack, int x, int y, int width, int height) {
		int opacity = mouseOver ? 255 : (int) (time * 256F / 25F);
		if (opacity > 255) opacity = 255;

		// Black back
		int colour = opacity << 24;
		AbstractGui.fill(matrixStack, x + 1, y, x + width - 1, y + height, colour);
		AbstractGui.fill(matrixStack, x, y + 1, x + 1, y + height - 1, colour);
		AbstractGui.fill(matrixStack, x + width - 1, y + 1, x + width, y + height - 1, colour);

		// Border
		colour = 0x28025c | opacity << 24;
		AbstractGui.fill(matrixStack, x + 1, y + 1, x + width - 1, y + 2, colour);
		AbstractGui.fill(matrixStack, x + 1, y + height - 1, x + width - 1, y + height - 2, colour);
		AbstractGui.fill(matrixStack, x + 1, y + 1, x + 2, y + height - 1, colour);
		AbstractGui.fill(matrixStack, x + width - 1, y + 1, x + width - 2, y + height - 1, colour);
	}
}
