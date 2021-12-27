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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import uk.kihira.tails.common.Tails;

/**
 * A specialized version of the {@link AbstractSliderButton} for {@code HSB} and {@code RGB} values.<br>
 * Also has tooltip support, as if it couldn't get any better.
 */
public class HSBSlider extends AbstractSliderButton implements ITooltip {

	private static final ResourceLocation SLIDER_TEXTURE = new ResourceLocation(Tails.MOD_ID, "texture/gui/controls/slider_hue.png");

	private final HSBSliderType type;
	private final IHSBSliderCallback callback;
	private float hueValue;
	private float briValue;
	private List<FormattedCharSequence> tooltips;

	public HSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
		super(xPos, yPos, width, height, new TextComponent(""), 0);
		this.type = type;
		hueValue = 0;
		briValue = 0;
		this.callback = callback;
	}

	public HSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, Component... tooltips) {
		this(id, xPos, yPos, width, height, callback, type);
		this.tooltips = Arrays.stream(tooltips).map(Component::getVisualOrderText).collect(Collectors.toList());
	}

	@Override
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partial) {
		if (visible) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
			RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

			if (type == HSBSliderType.SATURATION) {
				final Color hueColour = Color.getHSBColor(hueValue, 1F, 1F);
				final float red = (float) hueColour.getRed() / 255;
				final float green = (float) hueColour.getGreen() / 255;
				final float blue = (float) hueColour.getBlue() / 255;
				RenderSystem.setShaderColor(red, green, blue, 1.0F);
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
				RenderSystem.setShaderColor(red, green, blue, 1F);
				drawTexturedModalRectScaled(matrixStack, x + 1, y + 1, 0, srcY, 231, 20, width - 2, height - 2);
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			} else {
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
				drawTexturedModalRectScaled(matrixStack, x + 1, y + 1, 0, srcY, 256, 20, width - 2, height - 2);
			}

			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);
			blit(matrixStack, x + (int)(value * (width - 3) - 2), y, 0, 0, 7, 4);
			blit(matrixStack, x + (int)(value * (width - 3) - 2), y + height - 4, 7, 0, 7, 4);
		}
	}

	public HSBSliderType getType() {
		return type;
	}

	public double getValue() {
		return value;
	}

	/**
	 * Sets the current slider value between 0-1F
	 * @param value New value
	 */
	public void setValue(double value) { // Copied from setSliderValue (private)
		final double oldValue = value;
		this.value = Mth.clamp(value, 0.0D, 1.0D);

		updateMessage();
	}

	/**
	 * Sets the current slider value between 0-1F and calls the callback
	 * @param value New value
	 */
	public void setValueWithCallback(double value) {
		setValue(value);
		applyValue();
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

	void drawTexturedModalRectScaled(PoseStack matrixStack, int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
		final float f = 0.00390625F;
		final float f1 = 0.00390625F;
		final PoseStack.Pose e = matrixStack.last();
		final BufferBuilder renderer = Tesselator.getInstance().getBuilder();
		renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		renderer.vertex(e.pose(), x + 0,			y + tarHeight,	getBlitOffset()).uv((u + 0) * f, (v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + tarHeight,	getBlitOffset()).uv((u + srcWidth) * f, (v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + 0,			getBlitOffset()).uv((u + srcWidth) * f, (v + 0) * f1).endVertex();
		renderer.vertex(e.pose(), x + 0,			y + 0,			getBlitOffset()).uv((u + 0) * f, (v + 0) * f1).endVertex();
		renderer.end();
		BufferUploader.end(renderer);
	}

	@Override
	public List<FormattedCharSequence> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
		return tooltips;
	}

	@Override
	protected void applyValue() { // save
		if (callback != null) callback.onValueChangeHSBSlider(this, value);
	}

	@Override
	protected void updateMessage() { // updateMessage

	}

	public static enum HSBSliderType {
		HUE, SATURATION, BRIGHTNESS
	}

	public static interface IHSBSliderCallback {
		public void onValueChangeHSBSlider(HSBSlider source, double sliderValue);
	}
}
