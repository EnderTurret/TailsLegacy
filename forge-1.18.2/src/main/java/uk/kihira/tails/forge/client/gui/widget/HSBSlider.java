/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.gui.ScreenUtils;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.forge.client.RenderHelper;

/**
 * A specialized version of the {@link AbstractSliderButton} for {@code HSB} and {@code RGB} values.
 * Also has tooltip support, as if it couldn't get any better.
 */
public class HSBSlider extends AbstractSliderButton {

	protected static final ResourceLocation SLIDER_TEXTURE = ResourceLocation.fromNamespaceAndPath(TailsPlatform.MOD_ID, "textures/gui/controls/slider_hue.png");

	private final HSBSliderType type;
	private final IHSBSliderCallback callback;

	protected Screen tooltipScreen;
	protected Component tooltip;

	public HSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
		super(xPos, yPos, width, height, Component.empty(), 0);
		this.type = type;
		this.callback = callback;
	}

	public HSBSlider(int xPos, int yPos, IHSBSliderCallback callback, HSBSliderType type) {
		this(xPos, yPos, 100, 10, callback, type);
	}

	public HSBSlider setTooltip(Screen screen, Component value) {
		tooltipScreen = screen;
		tooltip = value;
		return this;
	}

	@Override
	public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
		if (tooltip != null)
			tooltipScreen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {
		ScreenUtils.blitWithBorder(poseStack, SLIDER_TEXTURE, x, y, 0, 10, width, height, 200, 20, 2, 3, 2, 2, 0);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);
		RenderHelper.blitScaled(poseStack, x + 1, y + 1, 0, 0, 236 - (type == HSBSliderType.BRIGHTNESS ? 20 : 0), 256, 20, width - 2, height - 2);

		final int offset = isFocused() ? 5 : 0;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		blit(poseStack, x + (int)(value * (width - 3) - 2), y, 0, offset, 7, 4);
		blit(poseStack, x + (int)(value * (width - 3) - 2), y + height - 4, 7, offset, 7, 4);
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
		this.value = TailsMath.clamp(value, 0D, 1D);

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