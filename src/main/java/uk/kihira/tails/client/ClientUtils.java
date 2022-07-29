/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.UUID;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.UUIDUtil;

public class ClientUtils {

	/**
	 * Draws a string that respects new lines.
	 * @param poseStack The {@link PoseStack} to use for transformation information.
	 * @param fontRenderer The {@link Font} to use for drawing the text.
	 * @param string The text to draw.
	 * @param x The x position of the text.
	 * @param y The y position of the text.
	 * @param color The color of the text.
	 */
	public static void drawStringMultiLine(PoseStack poseStack, Font fontRenderer, String string, int x, int y, int color) {
		final String[] lines = string.split("\n");
		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			fontRenderer.draw(poseStack, line, x, y + fontRenderer.lineHeight * i, color);
		}
	}

	public static void drawCenteredString(PoseStack poseStack, Font fontRenderer, String string, int x, int y, int color) {
		final int width = fontRenderer.width(string);
		fontRenderer.draw(poseStack, string, x - width / 2, y, color);
	}

	public static void blitScaled(PoseStack poseStack, int x, int y, int blitOffset, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
		final float f = 0.00390625F;
		final float f1 = 0.00390625F;
		final PoseStack.Pose e = poseStack.last();
		final Tesselator tess = Tesselator.getInstance();
		final BufferBuilder renderer = tess.getBuilder();
		renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		renderer.vertex(e.pose(), x + 0,		y + tarHeight,	blitOffset).uv((u + 0) * f,			(v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + tarHeight,	blitOffset).uv((u + srcWidth) * f,	(v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + 0,			blitOffset).uv((u + srcWidth) * f,	(v + 0) * f1).endVertex();
		renderer.vertex(e.pose(), x + 0,		y + 0,			blitOffset).uv((u + 0) * f,			(v + 0) * f1).endVertex();
		tess.end();
	}

	public static UUID getPlayerUUID() {
		final Minecraft mc = Minecraft.getInstance();
		/*if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();*/
		return UUIDUtil.getOrCreatePlayerUUID(mc.getUser().getGameProfile());
	}
}