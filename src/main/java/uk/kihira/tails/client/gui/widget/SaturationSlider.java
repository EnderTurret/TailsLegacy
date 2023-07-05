/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.RenderHelper;

/**
 * A version of {@link HSBSlider} for saturation and RGB values.
 * This is split off because it needs extra values for rendering.
 */
public class SaturationSlider extends HSBSlider {

	private float hueValue = 0;
	private float briValue = 0;

	public SaturationSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback) {
		super(xPos, yPos, width, height, callback, HSBSlider.HSBSliderType.SATURATION);
	}

	public SaturationSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, Component... tooltip) {
		super(xPos, yPos, width, height, callback, HSBSlider.HSBSliderType.SATURATION, tooltip);
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partial) {
		gui.blitWithBorder(WIDGETS_LOCATION, getX(), getY(), 0, 46, width, height, 200, 20, 2, 3, 2, 2);
		RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

		{
			final Color hueColour = Color.getHSBColor(hueValue, 1, 1);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			RenderSystem.setShaderColor(red, green, blue, 1F);
			RenderHelper.blitScaled(gui, getX() + 1, getY() + 1, 0, 0, 176, 256, 20, width - 2, height - 2);
		}

		final int srcY = 236 - 40;

		{
			final Color hueColour = Color.getHSBColor(0, 0, briValue);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			RenderSystem.setShaderColor(red, green, blue, 1F);
			RenderHelper.blitScaled(gui, getX() + 1, getY() + 1, 0, 0, srcY, 231, 20, width - 2, height - 2);
		}

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		final int offset = isFocused() ? 5 : 0;

		gui.blit(SLIDER_TEXTURE, getX() + (int)(value * (width - 3) - 2), getY(), 0, offset, 7, 4);
		gui.blit(SLIDER_TEXTURE, getX() + (int)(value * (width - 3) - 2), getY() + height - 4, 7, offset, 7, 4);
	}

	/**
	 * Sets the current hue value between 0-1F
	 * @param value New value
	 */
	public void setHue(float value) {
		hueValue = value;
	}

	/**
	 * Sets the current brightness value between 0-1F
	 * @param value New value
	 */
	public void setBrightness(float value) {
		briValue = value;
	}
}