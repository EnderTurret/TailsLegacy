/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.neoforge.common.Tails;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends Button implements ITooltip {

	public static final ResourceLocation ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, "textures/gui/icons.png");

	protected final TailsIcons icon;
	private final List<FormattedCharSequence> tooltip;

	public IconButton(int x, int y, TailsIcons icon, OnPress onPress, Component... tooltips) {
		super(x, y, 16 ,16, Component.empty(), onPress, DEFAULT_NARRATION);
		this.icon = icon;
		tooltip = Arrays.stream(tooltips).map(Component::getVisualOrderText).collect(Collectors.toList());
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		final int textureOffset = getYImage();

		gui.blit(ICONS_TEXTURE, getX(), getY(), icon.u, icon.v + textureOffset * 16, 16, 16);
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

	@Override
	public List<FormattedCharSequence> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
		return tooltip;
	}

	/**
	 * A toggle-able variant of the normal everyday icon button.
	 */
	public static class Toggle extends IconButton {

		public boolean toggled;

		public Toggle(int x, int y, TailsIcons icon, OnPress onPress, Component... tooltips) {
			super(x, y, icon, onPress, tooltips);
		}

		@Override
		protected int getYImage() {
			return toggled ? 2 : super.getYImage();
		}

		@Override
		public void onPress() {
			toggled = !toggled;
			super.onPress();
		}
	}
}
