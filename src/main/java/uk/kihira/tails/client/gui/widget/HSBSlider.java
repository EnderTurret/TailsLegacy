/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import uk.kihira.tails.common.Tails;

public class HSBSlider extends AbstractSlider implements ITooltip {

	private static final ResourceLocation sliderTexture = new ResourceLocation(Tails.MOD_ID, "texture/gui/controls/slider_hue.png");

	private final HSBSliderType type;
	private final IHSBSliderCallback callback;
	private float hueValue;
	private float briValue;
	private List<IReorderingProcessor> tooltips;

	public HSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
		super(xPos, yPos, width, height, new StringTextComponent(""), 0);
		this.type = type;
		hueValue = 0;
		briValue = 0;
		this.callback = callback;
	}

	public HSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, ITextComponent ... tooltips) {
		this(id, xPos, yPos, width, height, callback, type);
		this.tooltips = Arrays.stream(tooltips).map(ITextComponent::func_241878_f).collect(Collectors.toList());
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
		if (visible) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
			Minecraft.getInstance().getTextureManager().bindTexture(sliderTexture);

			if (type == HSBSliderType.SATURATION) {
				final Color hueColour = Color.getHSBColor(hueValue, 1F, 1F);
				final float red = (float) hueColour.getRed() / 255;
				final float green = (float) hueColour.getGreen() / 255;
				final float blue = (float) hueColour.getBlue() / 255;
				RenderSystem.color4f(red, green, blue, 1.0F);
				drawTexturedModalRectScaled(matrixStack, x + 1, y + 1, 0, 176, 256, 20, width - 2, height - 2);
			}

			int srcY = 236;

			if (type == HSBSliderType.BRIGHTNESS)
				srcY -= 20;

			if (type == HSBSliderType.SATURATION) {
				srcY -= 40;

				final Color hueColour = Color.getHSBColor(0F, 0F, briValue);
				final float red = (float) hueColour.getRed() / 255;
				final float green = (float) hueColour.getGreen() / 255;
				final float blue = (float) hueColour.getBlue() / 255;
				RenderSystem.color4f(red, green, blue, 1F);
				drawTexturedModalRectScaled(matrixStack, x + 1, y + 1, 0, srcY, 231, 20, width - 2, height - 2);
				RenderSystem.color4f(1F, 1F, 1F, 1F);
			} else {
				RenderSystem.color4f(1F, 1F, 1F, 1F);
				drawTexturedModalRectScaled(matrixStack, x + 1, y + 1, 0, srcY, 256, 20, width - 2, height - 2);
			}

			RenderSystem.color4f(1F, 1F, 1F, 1F);
			Minecraft.getInstance().getTextureManager().bindTexture(sliderTexture);
			blit(matrixStack, x + (int)(sliderValue * (width - 3) - 2), y, 0, 0, 7, 4);
			blit(matrixStack, x + (int)(sliderValue * (width - 3) - 2), y + height - 4, 7, 0, 7, 4);
		}
	}

	public HSBSliderType getType() {
		return type;
	}

	public double getValue() {
		return sliderValue;
	}

	/**
	 * Sets the current slider value between 0-1F
	 * @param value New value
	 */
	public void setValue(double value) { // Copied from setSliderValue (private)
		final double oldValue = sliderValue;
		sliderValue = MathHelper.clamp(value, 0.0D, 1.0D);

		func_230979_b_();
	}

	/**
	 * Sets the current slider value between 0-1F and calls the callback
	 * @param value New value
	 */
	public void setValueWithCallback(double value) {
		setValue(value);
		func_230972_a_();
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

	void drawTexturedModalRectScaled(MatrixStack matrixStack, int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
		final float f = 0.00390625F;
		final float f1 = 0.00390625F;
		final MatrixStack.Entry e = matrixStack.getLast();
		final BufferBuilder renderer = Tessellator.getInstance().getBuffer();
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(e.getMatrix(), x + 0,			y + tarHeight,	getBlitOffset()).tex((u + 0) * f, (v + srcHeight) * f1).endVertex();
		renderer.pos(e.getMatrix(), x + tarWidth,	y + tarHeight,	getBlitOffset()).tex((u + srcWidth) * f, (v + srcHeight) * f1).endVertex();
		renderer.pos(e.getMatrix(), x + tarWidth,	y + 0,			getBlitOffset()).tex((u + srcWidth) * f, (v + 0) * f1).endVertex();
		renderer.pos(e.getMatrix(), x + 0,			y + 0,			getBlitOffset()).tex((u + 0) * f, (v + 0) * f1).endVertex();
		renderer.finishDrawing();
		WorldVertexBufferUploader.draw(renderer);
	}

	@Override
	public List<IReorderingProcessor> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
		return tooltips;
	}

	@Override
	protected void func_230972_a_() { // save
		if (callback != null) callback.onValueChangeHSBSlider(this, sliderValue);
	}

	@Override
	protected void func_230979_b_() { // updateMessage

	}

	public enum HSBSliderType {
		HUE, SATURATION, BRIGHTNESS
	}

	public interface IHSBSliderCallback {
		void onValueChangeHSBSlider(HSBSlider source, double sliderValue);
	}
}
