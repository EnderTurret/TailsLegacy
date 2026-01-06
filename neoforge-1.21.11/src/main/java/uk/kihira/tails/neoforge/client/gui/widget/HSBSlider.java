/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.neoforge.client.RenderHelper;

/**
 * A specialized version of the {@link AbstractSliderButton} for {@code HSB} and {@code RGB} values.
 * Also has tooltip support, as if it couldn't get any better.
 */
public class HSBSlider extends AbstractSliderButton {

	public static final Identifier VANILLA_SLIDER_SPRITE = Identifier.withDefaultNamespace("widget/slider");
	protected static final Identifier SLIDER_TEXTURE = Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "textures/gui/controls/slider_hue.png");

	private final HSBSliderType type;
	private final IHSBSliderCallback callback;

	public HSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
		super(xPos, yPos, width, height, Component.empty(), 0);
		this.type = type;
		this.callback = callback;
	}

	public HSBSlider(int xPos, int yPos, IHSBSliderCallback callback, HSBSliderType type) {
		this(xPos, yPos, 100, 10, callback, type);
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partial) {
		gui.blitSprite(RenderPipelines.GUI_TEXTURED, VANILLA_SLIDER_SPRITE, getX(), getY(), width, height);

		RenderHelper.blitScaled(gui, SLIDER_TEXTURE,
				getX() + 1, getY() + 1, 0,
				0, 236 - (type == HSBSliderType.BRIGHTNESS ? 20 : 0),
				256, 20, width - 2, height - 2, 0xFFFFFFFF);

		final int offset = isFocused() ? 5 : 0;

		gui.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, getX() + (int)(value * (width - 3) - 2), getY(), 0, offset, 7, 4, 256, 256);
		gui.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, getX() + (int)(value * (width - 3) - 2), getY() + height - 4, 7, offset, 7, 4, 256, 256);
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