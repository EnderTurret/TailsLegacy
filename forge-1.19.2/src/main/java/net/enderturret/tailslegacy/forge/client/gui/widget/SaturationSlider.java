/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.client.gui.ScreenUtils;

import net.enderturret.tailslegacy.forge.client.RenderHelper;

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

	public SaturationSlider(int xPos, int yPos, IHSBSliderCallback callback) {
		this(xPos, yPos, 100, 10, callback);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {
		ScreenUtils.blitWithBorder(poseStack, SLIDER_TEXTURE, x, y, 0, 10, width, height, 200, 20, 2, 3, 2, 2, 0);
		RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		{
			final Color hueColour = Color.getHSBColor(hueValue, 1, 1);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			RenderSystem.setShaderColor(red, green, blue, 1F);
			RenderHelper.blitScaled(poseStack, x + 1, y + 1, 0, 0, 176, 256, 20, width - 2, height - 2);
		}

		final int srcY = 236 - 40;

		{
			final Color hueColour = Color.getHSBColor(0, 0, briValue);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			RenderSystem.setShaderColor(red, green, blue, 1F);
			RenderHelper.blitScaled(poseStack, x + 1, y + 1, 0, 0, srcY, 231, 20, width - 2, height - 2);
		}

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		final int offset = isFocused() ? 5 : 0;

		blit(poseStack, x + (int)(value * (width - 3) - 2), y, 0, offset, 7, 4);
		blit(poseStack, x + (int)(value * (width - 3) - 2), y + height - 4, 7, offset, 7, 4);
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