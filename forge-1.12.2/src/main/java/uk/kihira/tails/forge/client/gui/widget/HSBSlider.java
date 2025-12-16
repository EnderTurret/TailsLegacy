/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.client.config.GuiUtils;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.forge.client.RenderHelper;

/**
 * A specialized version of the {@link GuiSlider} for {@code HSB} and {@code RGB} values.
 * Also has tooltip support, as if it couldn't get any better.
 */
public class HSBSlider extends GuiSlider implements TooltipProvider {

	protected static final ResourceLocation SLIDER_TEXTURE = new ResourceLocation(TailsPlatform.MOD_ID, "textures/gui/controls/slider_hue.png");

	private final HSBSliderType type;
	private final IHSBSliderCallback callback;

	protected List<String> tooltip;

	public HSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
		super(callback, id, xPos, yPos, "", 0, 1, 0, (a, b, c) -> "");
		this.type = type;
		this.callback = callback;
		this.width = width;
		this.height = height;
	}

	public HSBSlider(int id, int xPos, int yPos, IHSBSliderCallback callback, HSBSliderType type) {
		this(id, xPos, yPos, 100, 10, callback, type);
	}

	public HSBSlider setTooltip(String value) {
		tooltip = Arrays.asList(value.split("\\n"));
		return this;
	}

	@Override
	public boolean isHovered(int mouseX, int mouseY) {
		return tooltip != null && isMouseOver();
	}

	@Override
	public List<String> getTooltip() {
		return tooltip;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible) return;

		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

		GuiUtils.drawContinuousTexturedBox(SLIDER_TEXTURE, x, y, 0, 10, width, height, 200, 20, 2, 3, 2, 2, 0);

		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(SLIDER_TEXTURE);
		RenderHelper.blitScaled(x + 1, y + 1, 0, 0, 236 - (type == HSBSliderType.BRIGHTNESS ? 20 : 0), 256, 20, width - 2, height - 2);

		final int offset = 0;//isFocused() ? 5 : 0;

		RenderHelper.enableDefaultBlend();

		drawTexturedModalRect(x + (int)(getSliderPosition() * (width - 3) - 2), y, 0, offset, 7, 4);
		drawTexturedModalRect(x + (int)(getSliderPosition() * (width - 3) - 2), y + height - 4, 7, offset, 7, 4);
	}

	private boolean disableRendering = false;

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		// Vanilla draws in this method, but we don't — disable it by no-op-ing the draw method.
		disableRendering = true;
		super.mouseDragged(mc, mouseX, mouseY);
		disableRendering = false;
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		if (!disableRendering)
			super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}

	public HSBSliderType getType() {
		return type;
	}

	public static enum HSBSliderType {
		HUE, SATURATION, BRIGHTNESS
	}

	public static interface IHSBSliderCallback extends GuiResponder {
		public void onValueChangeHSBSlider(int sourceId, double sliderValue);

		@Override
		public default void setEntryValue(int id, boolean value) {}

		@Override
		public default void setEntryValue(int id, float value) { onValueChangeHSBSlider(id, value); }

		@Override
		public default void setEntryValue(int id, String value) {}
	}
}