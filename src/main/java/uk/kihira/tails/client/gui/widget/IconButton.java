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

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import net.minecraft.client.gui.components.Button.OnPress;

/**
 * A button with an icon and a tooltip.
 */
public class IconButton extends Button implements ITooltip {

	public static final ResourceLocation iconsTextures = new ResourceLocation("tails", "texture/gui/icons.png");

	protected final Icons icon;
	private final List<FormattedCharSequence> tooltip;

	public IconButton(int x, int y, Icons icon, OnPress onPress, Component... tooltips) {
		super(x, y, 16 ,16, new TextComponent(""), onPress);
		this.icon = icon;
		tooltip = Arrays.stream(tooltips).map(Component::getVisualOrderText).collect(Collectors.toList());
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			RenderSystem.setShaderTexture(0, IconButton.iconsTextures);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			final int textureOffset = getYImage(isHovered);

			blit(matrixStack, x, y, icon.u, icon.v + textureOffset * 16, 16, 16);
		}
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
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
				toggled = !toggled;
				onPress();
				return true;
			}
			return false;
		}

		@Override
		public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (visible && toggled) {
				RenderSystem.setShaderTexture(0, IconButton.iconsTextures);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

				isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
				blit(matrixStack, x, y, icon.u, icon.v + 32, 16, 16);
			} else
				super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
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
