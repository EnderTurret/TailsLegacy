/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends Button implements ITooltip {

	public static final ResourceLocation iconsTextures = new ResourceLocation("tails", "textures/gui/icons.png");

	protected final Icons icon;
	private final List<FormattedCharSequence> tooltip;

	public IconButton(int x, int y, Icons icon, OnPress onPress, Component... tooltips) {
		super(x, y, 16 ,16, Component.empty(), onPress, DEFAULT_NARRATION);
		this.icon = icon;
		tooltip = Arrays.stream(tooltips).map(Component::getVisualOrderText).collect(Collectors.toList());
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderTexture(0, iconsTextures);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		final int textureOffset = getYImage();

		gui.blit(iconsTextures, getX(), getY(), icon.u, icon.v + textureOffset * 16, 16, 16);
	}

	protected int getYImage() {
		if (!active)
			return 0;
		else if (isHoveredOrFocused())
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

		public Toggle(int x, int y, Icons icon, OnPress onPress, Component... tooltips) {
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

	public enum Icons {
		UNDO(0, 0),
		QUESTION(16, 0),
		EYEDROPPER(32, 0),
		SAVE(48, 0),
		DELETE(64, 0),
		COPY(80, 0),
		STAR(96, 0),
		EDIT(112, 0),
		UPLOAD(128, 0),
		DOWNLOAD(144, 0),
		SEARCH(160, 0),
		SERVER(176, 0),
		IMPORT(192, 0),
		EXPORT(208, 0);

		public final int u;
		public final int v;

		private Icons(int u, int v) {
			this.u = u;
			this.v = v;
		}
	}
}
