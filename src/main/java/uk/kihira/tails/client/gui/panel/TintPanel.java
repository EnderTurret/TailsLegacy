/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.awt.Color;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.glfw.GLFW;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.ColorUtil;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.HSBSlider;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.gui.widget.RelativeTextBox;

@Internal
public final class TintPanel extends Panel<EditorScreen> implements HSBSlider.IHSBSliderCallback {

	private int editingTint = 0;
	private int currentTint = 0xFFFFFF;
	private EditBox hexText;
	private HSBSlider[] hsbSliders;
	private HSBSlider[] rgbSliders;
	private IconButton tintReset;
	private IconButton colourPicker;
	private boolean selectingColour = false;
	private int editPaneTop;

	public TintPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
		alwaysReceiveMouse = true;
	}

	@Override
	public void init() {
		editPaneTop = height - 107;
		// Edit tint buttons
		int topOffset = 20;
		for (int id = 2; id <= 4; id++) {
			final int finalId = id;
			addRenderableWidget(new Button(30, topOffset, 40, 20, Component.translatable("tails.gui.button.edit"), b -> handleTintButton(finalId)));
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new RelativeTextBox(font, 30, editPaneTop + 20, 73, 10, null);
		hexText.setMaxLength(6);
		addWidget(hexText);

		// RGB sliders
		rgbSliders = new HSBSlider[3];
		rgbSliders[0] = new HSBSlider(5, 5, editPaneTop + 70, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, Component.translatable("tails.gui.slider.red.tooltip"));
		rgbSliders[1] = new HSBSlider(6, 5, editPaneTop + 80, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, Component.translatable("tails.gui.slider.green.tooltip"));
		rgbSliders[2] = new HSBSlider(7, 5, editPaneTop + 90, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, Component.translatable("tails.gui.slider.blue.tooltip"));
		rgbSliders[0].setHue(0);
		rgbSliders[1].setHue(1F / 3F);
		rgbSliders[2].setHue(2F / 3F);

		addRenderableWidget(rgbSliders[0]);
		addRenderableWidget(rgbSliders[1]);
		addRenderableWidget(rgbSliders[2]);

		// HSB sliders
		hsbSliders = new HSBSlider[3];
		hsbSliders[0] = new HSBSlider(15, 5, editPaneTop + 35, 100, 10, this, HSBSlider.HSBSliderType.HUE, Component.translatable("tails.gui.slider.hue.tooltip"));
		hsbSliders[1] = new HSBSlider(16, 5, editPaneTop + 45, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, Component.translatable("tails.gui.slider.saturation.tooltip"));
		hsbSliders[2] = new HSBSlider(17, 5, editPaneTop + 55, 100, 10, this, HSBSlider.HSBSliderType.BRIGHTNESS, Component.translatable("tails.gui.slider.brightness.tooltip"));

		addRenderableWidget(hsbSliders[0]);
		addRenderableWidget(hsbSliders[1]);
		addRenderableWidget(hsbSliders[2]);

		// Reset/Save
		addRenderableWidget(tintReset = new IconButton(right - left - 20, editPaneTop + 2, IconButton.Icons.UNDO, b -> {
			final int newTint = parent.getOriginalPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
			refreshTintPane(newTint);
			tintReset.active = false;
		}, Component.translatable("tails.gui.button.reset")));
		tintReset.active = false;

		// Color Picker
		addRenderableWidget(colourPicker = new IconButton(right - left - 36, editPaneTop + 1, IconButton.Icons.EYEDROPPER, b -> setSelectingColour(true), Component.translatable("tails.gui.button.picker.0"), Component.translatable("tails.gui.button.picker.1")));
		colourPicker.visible = false;
		colourPicker.active = false;

		refreshTintPane(currentTint, true);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		setBlitOffset(-100);
		fillGradient(poseStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);

		setBlitOffset(0);

		// Tints
		int topOffset = 10;
		for (int tint = 1; tint <= 3; tint++) {
			final int colour = parent.getEditingPartInfo().getTints()[tint - 1] | 0xFF << 24;
			fillGradient(poseStack, 5, topOffset + 10, 25, topOffset + 30, colour, colour);
			font.draw(poseStack, I18n.get("tails.gui.tint", tint), 5, topOffset, 0xFFFFFF);
			topOffset += 35;
		}

		// Editing tint pane
		if (editingTint > 0) {
			hLine(poseStack, 0, width, editPaneTop, 0xFF000000);
			font.draw(poseStack, I18n.get("tails.gui.tint.edit", editingTint), 5, editPaneTop + 5, 0xFFFFFF);

			font.draw(poseStack, I18n.get("tails.gui.hex") + ":", 5, editPaneTop + 21, 0xFFFFFF);

			hexText.render(poseStack, mouseX, mouseY, partialTick);
		}

		super.render(poseStack, mouseX, mouseY, partialTick);
	}

	protected void handleTintButton(int id) {
		editingTint = id - 1;
		final int newTint = parent.getEditingPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
		refreshTintPane(newTint);
		tintReset.active = false;
		//colourPicker.active = true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (hexText.keyPressed(keyCode, scanCode, modifiers)) {
			try {
				if (!Strings.isNullOrEmpty(hexText.getValue()))
					refreshTintPane(Integer.parseInt(hexText.getValue(), 16));
			} catch (NumberFormatException ignored) {}

			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_ESCAPE && selectingColour) {
			setSelectingColour(false);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (hexText.charTyped(codePoint, modifiers)) {
			try {
				if (!Strings.isNullOrEmpty(hexText.getValue()))
					refreshTintPane(Integer.parseInt(hexText.getValue(), 16));
			} catch (NumberFormatException ignored) {}

			return true;
		}

		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (selectingColour && mouseButton == 0) {
			final int newTint = getColourAtPoint(mouseX, mouseY); // Ignore alpha.
			setSelectingColour(false);
			refreshTintPane(newTint);
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onValueChangeHSBSlider(HSBSlider source, double sliderValue) {
		final int newTint;

		if (source == rgbSliders[0] || source == rgbSliders[1] || source == rgbSliders[2])
			newTint = new Color(
					(int) Mth.clamp(rgbSliders[0].getValue() * 255F, 0, 255),
					(int) Mth.clamp(rgbSliders[1].getValue() * 255F, 0, 255),
					(int) Mth.clamp(rgbSliders[2].getValue() * 255F, 0, 255)).getRGB();

		else {
			final float[] hsbvals = {(float) hsbSliders[0].getValue(), (float) hsbSliders[1].getValue(), (float) hsbSliders[2].getValue()};
			hsbvals[source.getType().ordinal()] = (float) sliderValue;
			newTint = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
		}

		refreshTintPane(newTint);
	}

	private static int getColourAtPoint(double x, double y) {
		// TODO: Fix color picking.
		return 0xFF0000;
		/*final ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(3);

		GL11.glReadBuffer(GL11.GL_FRONT);

		GL11.glReadPixels((int) x, height - (int) y, 1, 1,
				GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE,
				pixelBuffer);

		final int r = pixelBuffer.get() & 0xFF;
		final int g = pixelBuffer.get() & 0xFF;
		final int b = pixelBuffer.get() & 0xFF;

		return (r << 16) | (g << 8) | b;*/
	}

	private void setSelectingColour(boolean selectingColour) {
		this.selectingColour = selectingColour;

		/*if (selectingColour) TODO: Change cursor icon.
			try {
				final BufferedImage bufferedImage = ImageIO.read(minecraft.getResourceManager().getResource(GuiIconButton.iconsTextures).getInputStream());
				int[] pixelData;
				final int pixels = 16 * 16;
				pixelData = new int[pixels];
				final IntBuffer buffer = IntBuffer.wrap(bufferedImage.getRGB(GuiIconButton.Icons.EYEDROPPER.u, GuiIconButton.Icons.EYEDROPPER.v + 16, 16, 16, pixelData, 0, 16));
				Cursor cursor = new Cursor(16, 16, 0, 15, 1, buffer, null);

				Mouse.setNativeCursor(cursor);
			} catch (IOException e) {
				Tails.LOGGER.error("Exception setting mouse cursor:", e);
			}
		else {
			try {
                Mouse.setNativeCursor(null);
            } catch (LWJGLException e) {
				Tails.LOGGER.error("Exception resetting mouse cursor:", e);
            }
		}*/
	}

	public void refreshTintPane(int newTint) {
		refreshTintPane(newTint, false);
	}

	public void refreshTintPane(int newTint, boolean force) {
		if (!force && newTint == currentTint) return;

		currentTint = newTint;

		hexText.setTextColor(currentTint);
		hexText.setValue(ColorUtil.hex(currentTint, true, true));

		// RGB Sliders
		final Color c = new Color(currentTint);
		rgbSliders[0].setValue(c.getRed() / 255F);
		rgbSliders[1].setValue(c.getGreen() / 255F);
		rgbSliders[2].setValue(c.getBlue() / 255F);

		// HSB Sliders
		final float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hsbSliders[0].setValue(hsbvals[0]);
		hsbSliders[1].setValue(hsbvals[1]);
		hsbSliders[2].setValue(hsbvals[2]);
		// The saturation slider needs to know the value of the other 2 sliders.
		hsbSliders[1].setHue((float) hsbSliders[0].getValue());
		hsbSliders[1].setBrightness((float) hsbSliders[2].getValue());

		if (editingTint > 0) {
			rgbSliders[0].visible = rgbSliders[1].visible = rgbSliders[2].visible = true;
			hsbSliders[0].visible = hsbSliders[1].visible = hsbSliders[2].visible = true;
			tintReset.visible = true;
			colourPicker.visible = true;
		} else {
			rgbSliders[0].visible = rgbSliders[1].visible = rgbSliders[2].visible = false;
			hsbSliders[0].visible = hsbSliders[1].visible = hsbSliders[2].visible = false;
			tintReset.visible = false;
			colourPicker.visible = false;
		}

		tintReset.active = true;

		if (editingTint > 0)
			parent.getEditingPartInfo().getTints()[editingTint - 1] = currentTint | 0xFF000000; // Add the alpha manually.

		parent.setPartsInfo(parent.getEditingPartInfo());
	}

	public void setEditingTint(int value) {
		editingTint = value;
		refreshTintPane(currentTint, true);
	}

	public int getEditingTint() {
		return editingTint;
	}
}
