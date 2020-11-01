package uk.kihira.tails.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;

public class RelativeTextField extends TextFieldWidget {

	private MatrixStack matrixStack;

	public RelativeTextField(FontRenderer font, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_) {
		super(font, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.matrixStack = matrixStack;
		super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		this.matrixStack = null;
	}

	// Fixes TextFieldWidget#drawSelectionBox not taking into account MatrixStack transformations.
	@Override
	public void drawSelectionBox(int startX, int startY, int endX, int endY) {
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

	      if (endX > this.x + this.width) {
	         endX = this.x + this.width;
	      }

	      if (startX > this.x + this.width) {
	         startX = this.x + this.width;
	      }

	      Tessellator tessellator = Tessellator.getInstance();
	      BufferBuilder bufferbuilder = tessellator.getBuffer();
	      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
	      RenderSystem.disableTexture();
	      RenderSystem.enableColorLogicOp();
	      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
	      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
	      bufferbuilder.pos(matrixStack.getLast().getMatrix(), startX, endY, 0F).endVertex();
	      bufferbuilder.pos(matrixStack.getLast().getMatrix(), endX, endY, 0F).endVertex();
	      bufferbuilder.pos(matrixStack.getLast().getMatrix(), endX, startY, 0F).endVertex();
	      bufferbuilder.pos(matrixStack.getLast().getMatrix(), startX, startY, 0F).endVertex();
	      tessellator.draw();
	      RenderSystem.disableColorLogicOp();
	      RenderSystem.enableTexture();
	   }
}