/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.gui.widget.ITooltip;

public abstract class BaseScreen extends Screen {

	private int prevMouseX;
	private int prevMouseY;
	private float mouseIdleTicks;

	protected BaseScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		for (Widget btn : buttons)
			if (btn instanceof ITooltip && btn.isMouseOver(mouseX, mouseY)) {
				if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTicks;
				else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;

				renderToolTip(matrixStack, ((ITooltip) btn).getTooltip(mouseX, mouseY, mouseIdleTicks), mouseX, mouseY, font);

				prevMouseX = mouseX;
				prevMouseY = mouseY;
				break;
			}
	}

	public void rect(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color) {
		hLine(matrixStack, x1, x2, y1, color);
		hLine(matrixStack, x1, x2, y2, color);
		vLine(matrixStack, x1, y1, y2, color);
		vLine(matrixStack, x2, y1, y2, color);
	}

	public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	/**
	 * A button that has a tooltip.
	 */
	public static class TooltipButton extends ExtendedButton implements ITooltip {

		private final int maxTextWidth;
		protected final List<IReorderingProcessor> tooltip = new ArrayList<>();
		protected final FontRenderer font;

		public TooltipButton(int x, int y, int width, int height, ITextComponent text, int maxTextWidth, IPressable pressable, FontRenderer font, ITextComponent... tooltips) {
			super(x, y, width, height, text, pressable);
			this.maxTextWidth = maxTextWidth;
			this.font = font;

			if (tooltips != null && tooltips.length > 0)
				for (ITextComponent s : tooltips)
					tooltip.addAll(font.trimStringToWidth(s, this.maxTextWidth));
		}

		@Override
		public List<IReorderingProcessor> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
			return tooltip;
		}
	}

	/**
	 * A toggle-able button with a tooltip.
	 */
	public class ToggleButton extends TooltipButton {

		public ToggleButton(int x, int y, int width, int height, ITextComponent text, int maxTextWidth, IPressable onPress, FontRenderer font, ITextComponent... tooltips) {
			super(x, y, width, height, text, maxTextWidth, onPress, font, tooltips);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (visible && button == 0 && BaseScreen.isMouseOver(mouseX, mouseY, x, y, width, height)) {
				active = !active;
				return true;
			}
			return false;
		}

		@Override
		public void renderWidget(MatrixStack matrixStack, int x, int y, float partialTicks) {
			final List<IReorderingProcessor> list = new ArrayList<>(tooltip);
			list.add(!active ? new StringTextComponent("Enabled").mergeStyle(TextFormatting.GREEN, TextFormatting.ITALIC).func_241878_f() : new StringTextComponent("Disabled").mergeStyle(TextFormatting.RED, TextFormatting.ITALIC).func_241878_f());
			BaseScreen.this.renderToolTip(matrixStack, list, x, y, font);
		}
	}
}
