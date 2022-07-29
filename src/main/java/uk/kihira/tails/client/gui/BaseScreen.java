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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.widget.ITooltip;

public abstract class BaseScreen extends Screen {

	private int prevMouseX;
	private int prevMouseY;
	private float mouseIdleTicks;

	protected BaseScreen(Component titleIn) {
		super(titleIn);
	}

	public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		for (Widget btn : renderables)
			if (btn instanceof ITooltip && btn instanceof GuiEventListener && ((GuiEventListener) btn).isMouseOver(mouseX, mouseY)) {
				if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTicks;
				else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;

				renderTooltip(poseStack, ((ITooltip) btn).getTooltip(mouseX, mouseY, mouseIdleTicks), mouseX, mouseY, font);

				prevMouseX = mouseX;
				prevMouseY = mouseY;
				break;
			}
	}

	public void rect(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {
		hLine(poseStack, x1, x2, y1, color);
		hLine(poseStack, x1, x2, y2, color);
		vLine(poseStack, x1, y1, y2, color);
		vLine(poseStack, x2, y1, y2, color);
	}

	public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	/**
	 * A button that has a tooltip.
	 */
	public static class TooltipButton extends ExtendedButton implements ITooltip {

		private final int maxTextWidth;
		protected final List<FormattedCharSequence> tooltip = new ArrayList<>();
		protected final Font font;

		public TooltipButton(int x, int y, int width, int height, Component text, int maxTextWidth, OnPress pressable, Font font, Component... tooltips) {
			super(x, y, width, height, text, pressable);
			this.maxTextWidth = maxTextWidth;
			this.font = font;

			if (tooltips != null && tooltips.length > 0)
				for (Component s : tooltips)
					tooltip.addAll(font.split(s, this.maxTextWidth));
		}

		@Override
		public List<FormattedCharSequence> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
			return tooltip;
		}
	}

	/**
	 * A toggle-able button with a tooltip.
	 */
	public class ToggleButton extends TooltipButton {

		public ToggleButton(int x, int y, int width, int height, Component text, int maxTextWidth, OnPress onPress, Font font, Component... tooltips) {
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
		public void renderButton(PoseStack poseStack, int x, int y, float partialTicks) {
			final List<FormattedCharSequence> list = new ArrayList<>(tooltip);
			list.add(!active ? Component.literal("Enabled").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC).getVisualOrderText() : Component.literal("Disabled").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC).getVisualOrderText());
			BaseScreen.this.renderTooltip(poseStack, list, x, y, font);
		}
	}
}
