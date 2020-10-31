/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class GuiBaseScreen extends Screen {
	private int prevMouseX;
	private int prevMouseY;
	private float mouseIdleTicks;

	protected GuiBaseScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		// Tooltips

	}

	public void renderTooltips(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		for (Widget btn : buttons) {
			if (btn instanceof ITooltip && btn.isMouseOver(mouseX, mouseY)) {
				if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTicks;
				else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;

				renderToolTip(matrixStack, ((ITooltip) btn).getTooltip(mouseX, mouseY, mouseIdleTicks), mouseX, mouseY, font);

				prevMouseX = mouseX;
				prevMouseY = mouseY;
				break;
			}
		}
	}

	public class GuiButtonTooltip extends ExtendedButton implements ITooltip {
		private final int maxTextWidth;
		protected final ArrayList<IReorderingProcessor> tooltip = new ArrayList<>();

		public GuiButtonTooltip(int x, int y, int width, int height, ITextComponent text, int maxTextWidth, IPressable pressable, ITextComponent... tooltips) {
			super(x, y, width, height, text, pressable);
			this.maxTextWidth = maxTextWidth;
			if (tooltips != null && tooltips.length > 0) {
				for (ITextComponent s : tooltips) {
					tooltip.addAll(font.trimStringToWidth(s, this.maxTextWidth));
				}
			}
		}

		@Override
		public List<IReorderingProcessor> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
			return this.tooltip;
		}
	}

	public class GuiButtonToggle extends GuiButtonTooltip {

		public GuiButtonToggle(int x, int y, int width, int height, ITextComponent text, int maxTextWidth, IPressable onPress, ITextComponent... tooltips) {
			super(x, y, width, height, text, maxTextWidth, onPress, tooltips);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.visible && button == 0 && GuiBaseScreen.isMouseOver(mouseX, mouseY, x, y, width, height)) {
				this.active = !this.active;
				return true;
			}
			return false;
		}

		@Override
		public void renderButton(MatrixStack matrixStack, int x, int y, float partialTicks) {
			ArrayList<IReorderingProcessor> list = new ArrayList<>(this.tooltip);
			list.add((!active ? new StringTextComponent("Enabled").mergeStyle(TextFormatting.GREEN, TextFormatting.ITALIC).func_241878_f() : new StringTextComponent("Disabled").mergeStyle(TextFormatting.RED, TextFormatting.ITALIC).func_241878_f()));
			GuiBaseScreen.this.renderToolTip(matrixStack, list, x, y, font);
		}
	}

	public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}
}
