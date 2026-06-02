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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraftforge.fml.client.config.GuiUtils;

import net.enderturret.tailslegacy.forge.client.RenderHelper;

/**
 * A version of {@link HSBSlider} for saturation and RGB values.
 * This is split off because it needs extra values for rendering.
 */
public class SaturationSlider extends HSBSlider {

	private float hueValue = 0;
	private float briValue = 0;

	public SaturationSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback) {
		super(id, xPos, yPos, width, height, callback, HSBSlider.HSBSliderType.SATURATION);
	}

	public SaturationSlider(int id, int xPos, int yPos, IHSBSliderCallback callback) {
		this(id, xPos, yPos, 100, 10, callback);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible) return;

		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		mouseDragged(mc, mouseX, mouseY);

		GuiUtils.drawContinuousTexturedBox(SLIDER_TEXTURE, x, y, 0, 10, width, height, 200, 20, 2, 3, 2, 2, 0);
		mc.getTextureManager().bindTexture(SLIDER_TEXTURE);

		RenderHelper.enableDefaultBlend();

		{
			final Color hueColour = Color.getHSBColor(hueValue, 1, 1);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			GlStateManager.color(red, green, blue, 1F);
			RenderHelper.blitScaled(x + 1, y + 1, 0, 0, 176, 256, 20, width - 2, height - 2);
		}

		final int srcY = 236 - 40;

		{
			final Color hueColour = Color.getHSBColor(0, 0, briValue);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			GlStateManager.color(red, green, blue, 1F);
			RenderHelper.blitScaled(x + 1, y + 1, 0, 0, srcY, 231, 20, width - 2, height - 2);
		}

		GlStateManager.color(1F, 1F, 1F, 1F);

		final int offset = isMouseDown ? 5 : 0;

		drawTexturedModalRect(x + (int)(getSliderPosition() * (width - 3) - 2), y, 0, offset, 7, 4);
		drawTexturedModalRect(x + (int)(getSliderPosition() * (width - 3) - 2), y + height - 4, 7, offset, 7, 4);
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