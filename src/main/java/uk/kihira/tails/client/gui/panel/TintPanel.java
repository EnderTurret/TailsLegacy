/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.awt.Color;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.HSBSlider;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.gui.widget.RelativeTextField;

public class TintPanel extends Panel<EditorScreen> implements HSBSlider.IHSBSliderCallback {

	private int editingTint = 0;
	private int currentTint = 0xFFFFFF;
	private TextFieldWidget hexText;
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
			addButton(new Button(30, topOffset, 40, 20, new TranslationTextComponent("tails.gui.button.edit"), b -> handleTintButton(finalId)));
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new RelativeTextField(font, 30, editPaneTop + 20, 73, 10, null);
		hexText.setMaxStringLength(6);
		hexText.setText(Integer.toHexString(currentTint));
		addListener(hexText);

		// RGB sliders
		rgbSliders = new HSBSlider[3];
		rgbSliders[0] = new HSBSlider(5, 5, editPaneTop + 70, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("tails.gui.slider.red.tooltip"));
		rgbSliders[1] = new HSBSlider(6, 5, editPaneTop + 80, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("tails.gui.slider.green.tooltip"));
		rgbSliders[2] = new HSBSlider(7, 5, editPaneTop + 90, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("tails.gui.slider.blue.tooltip"));
		rgbSliders[0].setHue(0);
		rgbSliders[1].setHue(1F / 3F);
		rgbSliders[2].setHue(2F / 3F);

		addButton(rgbSliders[0]);
		addButton(rgbSliders[1]);
		addButton(rgbSliders[2]);

		// HSB sliders
		hsbSliders = new HSBSlider[3];
		hsbSliders[0] = new HSBSlider(15, 5, editPaneTop + 35, 100, 10, this, HSBSlider.HSBSliderType.HUE, new TranslationTextComponent("tails.gui.slider.hue.tooltip"));
		hsbSliders[1] = new HSBSlider(16, 5, editPaneTop + 45, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("tails.gui.slider.saturation.tooltip"));
		hsbSliders[2] = new HSBSlider(17, 5, editPaneTop + 55, 100, 10, this, HSBSlider.HSBSliderType.BRIGHTNESS, new TranslationTextComponent("tails.gui.slider.brightness.tooltip"));

		addButton(hsbSliders[0]);
		addButton(hsbSliders[1]);
		addButton(hsbSliders[2]);

		// Reset/Save
		addButton(tintReset = new IconButton(right - left - 20, editPaneTop + 2, IconButton.Icons.UNDO, b -> {
			currentTint = parent.getOriginalPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
			hexText.setText(Integer.toHexString(currentTint));
			refreshTintPane();
			tintReset.active = false;
		}, new TranslationTextComponent("tails.gui.button.reset")));
		tintReset.active = false;

		// Color Picker
		addButton(colourPicker = new IconButton(right - left - 36, editPaneTop + 1, IconButton.Icons.EYEDROPPER, b -> setSelectingColour(true), new TranslationTextComponent("tails.gui.button.picker.0"), new TranslationTextComponent("tails.gui.button.picker.1")));
		colourPicker.visible = false;
		colourPicker.active = false;

		refreshTintPane();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		setBlitOffset(-100);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);

		setBlitOffset(0);

		// Tints
		int topOffset = 10;
		for (int tint = 1; tint <= 3; tint++) {
			final int colour = parent.getEditingPartInfo().getTints()[tint - 1] | 0xFF << 24;
			fillGradient(matrixStack, 5, topOffset + 10, 25, topOffset + 30, colour, colour);
			font.drawString(matrixStack, I18n.format("tails.gui.tint", tint), 5, topOffset, 0xFFFFFF);
			topOffset += 35;
		}

		// Editing tint pane
		if (editingTint > 0) {
			hLine(matrixStack, 0, width, editPaneTop, 0xFF000000);
			font.drawString(matrixStack, I18n.format("tails.gui.tint.edit", editingTint), 5, editPaneTop + 5, 0xFFFFFF);

			font.drawString(matrixStack, I18n.format("tails.gui.hex") + ":", 5, editPaneTop + 21, 0xFFFFFF);

			hexText.render(matrixStack, mouseX, mouseY, partialTicks);
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	protected void handleTintButton(int id) {
		editingTint = id - 1;
		currentTint = parent.getEditingPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
		hexText.setText(Integer.toHexString(currentTint));
		refreshTintPane();
		tintReset.active = false;
		//colourPicker.active = true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (hexText.keyPressed(keyCode, scanCode, modifiers)) {
			try {
				if (!Strings.isNullOrEmpty(hexText.getText()))
					currentTint = Integer.parseInt(hexText.getText(), 16);
			} catch (NumberFormatException ignored) {}

			refreshTintPane();

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
				if (!Strings.isNullOrEmpty(hexText.getText()))
					currentTint = Integer.parseInt(hexText.getText(), 16);
			} catch (NumberFormatException ignored) {}

			refreshTintPane();

			return true;
		}

		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (selectingColour && mouseButton == 0) {
			currentTint = getColourAtPoint(mouseX, mouseY); // Ignore alpha.
			setSelectingColour(false);
			refreshTintPane();
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onValueChangeHSBSlider(HSBSlider source, double sliderValue) {
		if (source == rgbSliders[0] || source == rgbSliders[1] || source == rgbSliders[2])
			currentTint = new Color(
					(int) MathHelper.clamp(rgbSliders[0].getValue() * 255F, 0, 255),
					(int) MathHelper.clamp(rgbSliders[1].getValue() * 255F, 0, 255),
					(int) MathHelper.clamp(rgbSliders[2].getValue() * 255F, 0, 255)).getRGB();
		else {
			final float[] hsbvals = {(float) hsbSliders[0].getValue(), (float) hsbSliders[1].getValue(), (float) hsbSliders[2].getValue()};
			hsbvals[source.getType().ordinal()] = (float) sliderValue;
			currentTint = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
		}
		hexText.setText(Integer.toHexString(currentTint));
		refreshTintPane();
	}

	private int getColourAtPoint(double x, double y) {
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
				e.printStackTrace();
			}
		else {
			try {
                Mouse.setNativeCursor(null);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
		}*/
	}

	public void refreshTintPane() {
		hexText.setTextColor(currentTint);

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
	}

	public int getEditingTint() {
		return editingTint;
	}
}
