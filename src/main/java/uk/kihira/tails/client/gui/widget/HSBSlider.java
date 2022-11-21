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

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import net.minecraftforge.client.gui.ScreenUtils;

import uk.kihira.tails.client.RenderHelper;
import uk.kihira.tails.common.Tails;

/**
 * A specialized version of the {@link AbstractSliderButton} for {@code HSB} and {@code RGB} values.
 * Also has tooltip support, as if it couldn't get any better.
 */
public class HSBSlider extends AbstractSliderButton implements ITooltip {

	protected static final ResourceLocation SLIDER_TEXTURE = new ResourceLocation(Tails.MOD_ID, "textures/gui/controls/slider_hue.png");

	private final HSBSliderType type;
	private final IHSBSliderCallback callback;
	private List<FormattedCharSequence> tooltips;

	public HSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
		super(xPos, yPos, width, height, Component.empty(), 0);
		this.type = type;
		this.callback = callback;
	}

	public HSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, Component... tooltips) {
		this(xPos, yPos, width, height, callback, type);
		this.tooltips = Arrays.stream(tooltips).map(Component::getVisualOrderText).collect(Collectors.toList());
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {
		ScreenUtils.blitWithBorder(poseStack, WIDGETS_LOCATION, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
		RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

		int srcY = 236;

		if (type == HSBSliderType.BRIGHTNESS)
			srcY -= 20;

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderHelper.blitScaled(poseStack, x + 1, y + 1, getBlitOffset(), 0, srcY, 256, 20, width - 2, height - 2);

		blit(poseStack, x + (int)(value * (width - 3) - 2), y, 0, 0, 7, 4);
		blit(poseStack, x + (int)(value * (width - 3) - 2), y + height - 4, 7, 0, 7, 4);
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
		this.value = Mth.clamp(value, 0D, 1D);

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

	@Override
	public List<FormattedCharSequence> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
		return tooltips;
	}

	@Override
	protected void applyValue() {
		if (callback != null)
			callback.onValueChangeHSBSlider(this, value);
	}

	@Override
	protected void updateMessage() {}

	public static enum HSBSliderType {
		HUE, SATURATION, BRIGHTNESS
	}

	public static interface IHSBSliderCallback {
		public void onValueChangeHSBSlider(HSBSlider source, double sliderValue);
	}
}
