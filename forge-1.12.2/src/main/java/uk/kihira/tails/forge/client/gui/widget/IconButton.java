/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.widget;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.gui.TailsIcons;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends GuiButton {

	public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(TailsPlatform.MOD_ID, "textures/gui/icons.png");

	protected final TailsIcons icon;

	protected GuiScreen tooltipScreen;
	protected String tooltip;

	public IconButton(int id, int x, int y, TailsIcons icon) {
		super(id, x, y, 16, 16, "");
		this.icon = icon;
	}

	public IconButton setTooltip(GuiScreen screen, String value) {
		tooltipScreen = screen;
		tooltip = value;
		return this;
	}

	// TODO Tooltips
	public void onTooltip(int mouseX, int mouseY) {
		if (tooltip != null)
			tooltipScreen.drawHoveringText(tooltip, mouseX, mouseY);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1F, 1F, 1F, 1F);

		final int textureOffset = getYImage();

		mc.getTextureManager().bindTexture(ICONS_TEXTURE);
		drawModalRectWithCustomSizedTexture(x, y, icon.u, icon.v + textureOffset * 16, 16, 16, 256, 256);
	}

	protected int getYImage() {
		if (!enabled) return 0;
		if (hovered) return 2;
		return 1;
	}

	public void setHover(boolean hover) {
		hovered = hover;
	}

	/**
	 * A toggle-able variant of the normal everyday icon button.
	 */
	public static class Toggle extends IconButton {

		public boolean toggled;

		public Toggle(int id, int x, int y, TailsIcons icon) {
			super(id, x, y, icon);
		}

		@Override
		protected int getYImage() {
			return toggled ? 2 : super.getYImage();
		}

		public void onPress() {
			toggled = !toggled;
		}
	}
}