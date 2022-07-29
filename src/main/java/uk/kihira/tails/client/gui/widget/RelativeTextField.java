/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * An {@link EditBox} that takes into account the {@link PoseStack} transformations when drawing the selection box.<br>
 * Without this, the selection overlay will attempt to yeet itself as far off-screen as possible.
 * @author EnderTurret
 */
// TODO Rename to RelativeEditBox
public class RelativeTextField extends EditBox {

	private PoseStack poseStack;

	public RelativeTextField(Font font, int x, int y, int width, int height, Component title) {
		super(font, x, y, width, height, title);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.poseStack = poseStack;
		super.renderButton(poseStack, mouseX, mouseY, partialTicks);
		this.poseStack = null;
	}

	// Fixes TextFieldWidget#drawSelectionBox not taking into account MatrixStack transformations.
	@Override
	public void renderHighlight(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > x + width)
			endX = x + width;

		if (startX > x + width)
			startX = x + width;

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		bufferbuilder.vertex(poseStack.last().pose(), startX, endY, 0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), endX, endY, 0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), endX, startY, 0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), startX, startY, 0F).endVertex();
		tessellator.end();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
}