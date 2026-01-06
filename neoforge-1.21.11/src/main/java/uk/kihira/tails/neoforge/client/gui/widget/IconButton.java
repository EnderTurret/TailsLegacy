/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.gui.TailsIcons;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends Button {

	public static final Identifier ICONS_TEXTURE = Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "textures/gui/icons.png");

	protected final TailsIcons icon;

	public IconButton(int x, int y, TailsIcons icon, OnPress onPress) {
		super(x, y, 16, 16, Component.empty(), onPress, DEFAULT_NARRATION);
		this.icon = icon;
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		final int textureOffset = getYImage();

		gui.blit(RenderPipelines.GUI_TEXTURED, ICONS_TEXTURE, getX(), getY(), icon.u, icon.v + textureOffset * 16, 16, 16, 256, 256);
	}

	protected int getYImage() {
		if (!active)
			return 0;
		if (isHoveredOrFocused())
			return 2;
		return 1;
	}

	public void setHover(boolean hover) {
		isHovered = hover;
	}

	/**
	 * A toggle-able variant of the normal everyday icon button.
	 */
	public static class Toggle extends IconButton {

		public boolean toggled;

		public Toggle(int x, int y, TailsIcons icon, OnPress onPress) {
			super(x, y, icon, onPress);
		}

		@Override
		protected int getYImage() {
			return toggled ? 2 : super.getYImage();
		}

		@Override
		public void onPress(InputWithModifiers input) {
			toggled = !toggled;
			super.onPress(input);
		}
	}
}