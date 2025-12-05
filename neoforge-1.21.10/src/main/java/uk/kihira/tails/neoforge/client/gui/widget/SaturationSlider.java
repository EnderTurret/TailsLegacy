/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.widget;

import java.awt.Color;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

import uk.kihira.tails.neoforge.client.RenderHelper;

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
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partial) {
		gui.blitSprite(RenderPipelines.GUI_TEXTURED, VANILLA_SLIDER_SPRITE, getX(), getY(), width, height);

		{
			final Color hueColour = Color.getHSBColor(hueValue, 1, 1);
			RenderHelper.blitScaled(gui, SLIDER_TEXTURE, getX() + 1, getY() + 1, 0,
					0, 176, 256, 20, width - 2, height - 2, 0xFF000000 | hueColour.getRGB());
		}

		{
			final Color hueColour = Color.getHSBColor(0, 0, briValue);
			RenderHelper.blitScaled(gui, SLIDER_TEXTURE, getX() + 1, getY() + 1, 0,
					0, 196, 231, 20, width - 2, height - 2, 0xFF000000 | hueColour.getRGB());
		}

		final int offset = isFocused() ? 5 : 0;

		gui.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, getX() + (int)(value * (width - 3) - 2), getY(), 0, offset, 7, 4, 256, 256);
		gui.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, getX() + (int)(value * (width - 3) - 2), getY() + height - 4, 7, offset, 7, 4, 256, 256);
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