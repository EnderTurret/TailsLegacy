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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.gui.panel.Panel;

/**
 * An {@link EditBox} that takes into account the {@link PoseStack} transformations when drawing the selection box.
 * Without this, the selection overlay will attempt to yeet itself as far off-screen as possible.
 * @author EnderTurret
 */
public final class RelativeTextBox extends EditBox {

	private final Panel<?> parent;

	private PoseStack poseStack;

	public RelativeTextBox(Panel<?> parent, Font font, int x, int y, int width, int height, Component message) {
		super(font, x, y, width, height, message);
		this.parent = parent;
	}

	@Override
	public void setFocus(boolean isFocused) {
		super.setFocus(isFocused);
		parent.getParent().setFocusedPanel(parent);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		this.poseStack = poseStack;
		super.renderButton(poseStack, mouseX, mouseY, partialTick);
		this.poseStack = null;
	}

	// Fixes TextBox#renderHighlight not taking into account PoseStack transformations.
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
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		bufferbuilder.vertex(poseStack.last().pose(), startX, endY, 0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), endX, endY, 0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), endX, startY, 0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), startX, startY, 0F).endVertex();
		tessellator.end();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
}