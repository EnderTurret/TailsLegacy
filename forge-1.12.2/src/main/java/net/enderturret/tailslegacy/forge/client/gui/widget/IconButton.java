/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends GuiButton implements TooltipProvider {

	public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(TailsPlatform.MOD_ID, "textures/gui/icons.png");

	protected final TailsIcons icon;

	protected List<String> tooltip;

	public IconButton(int id, int x, int y, TailsIcons icon) {
		super(id, x, y, 16, 16, "");
		this.icon = icon;
	}

	public IconButton setTooltip(String value) {
		tooltip = Arrays.asList(value.split("\\n"));
		return this;
	}

	@Override
	public boolean isHovered(int mouseX, int mouseY) {
		return tooltip != null && isMouseOver();
	}

	@Override
	public List<String> getTooltip() {
		return tooltip;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible) return;
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

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