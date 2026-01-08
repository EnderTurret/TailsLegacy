/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.widget;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.client.config.GuiUtils;
import uk.kihira.tails.forge.client.RenderHelper;

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
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible) return;

		field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		mouseDragged(mc, mouseX, mouseY);

		GuiUtils.drawContinuousTexturedBox(SLIDER_TEXTURE, xPosition, yPosition, 0, 10, width, height, 200, 20, 2, 3, 2, 2, 0);
		mc.getTextureManager().bindTexture(SLIDER_TEXTURE);

		RenderHelper.enableDefaultBlend();

		{
			final Color hueColour = Color.getHSBColor(hueValue, 1, 1);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			GL11.glColor4f(red, green, blue, 1F);
			RenderHelper.blitScaled(xPosition + 1, yPosition + 1, 0, 0, 176, 256, 20, width - 2, height - 2);
		}

		final int srcY = 236 - 40;

		{
			final Color hueColour = Color.getHSBColor(0, 0, briValue);
			final float red = hueColour.getRed() / 255F;
			final float green = hueColour.getGreen() / 255F;
			final float blue = hueColour.getBlue() / 255F;
			GL11.glColor4f(red, green, blue, 1F);
			RenderHelper.blitScaled(xPosition + 1, yPosition + 1, 0, 0, srcY, 231, 20, width - 2, height - 2);
		}

		GL11.glColor4f(1F, 1F, 1F, 1F);

		final int offset = dragging ? 5 : 0;

		drawTexturedModalRect(xPosition + (int)(sliderValue * (width - 3) - 2), yPosition, 0, offset, 7, 4);
		drawTexturedModalRect(xPosition + (int)(sliderValue * (width - 3) - 2), yPosition + height - 4, 7, offset, 7, 4);
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