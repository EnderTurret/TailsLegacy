/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends Button implements Button.OnTooltip {

	public static final ResourceLocation ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath(TailsPlatform.MOD_ID, "textures/gui/icons.png");

	protected final TailsIcons icon;

	protected Screen tooltipScreen;
	protected Component tooltip;

	public IconButton(int x, int y, TailsIcons icon, OnPress onPress) {
		super(x, y, 16, 16, TextComponent.EMPTY, onPress);
		this.icon = icon;
	}

	public IconButton setTooltip(Screen screen, Component value) {
		tooltipScreen = screen;
		tooltip = value;
		return this;
	}

	@Override
	public void onTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
		if (tooltip != null)
			tooltipScreen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
	}

	@Override
	public void narrateTooltip(Consumer<Component> contents) {
		if (tooltip != null)
			contents.accept(tooltip);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		final int textureOffset = getYImage();

		RenderSystem.setShaderTexture(0, ICONS_TEXTURE);
		blit(poseStack, x, y, icon.u, icon.v + textureOffset * 16, 16, 16);
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
		public void onPress() {
			toggled = !toggled;
			super.onPress();
		}
	}
}
