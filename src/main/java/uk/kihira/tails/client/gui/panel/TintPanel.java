/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

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

public class TintPanel extends Panel<EditorScreen> implements HSBSlider.IHSBSliderCallback {

	int currTintEdit = 0;
	int currTintColour = 0xFFFFFF;
	private TextFieldWidget hexText;
	private HSBSlider[] hsbSliders;
	private HSBSlider[] rgbSliders;
	private IconButton tintReset;
	private IconButton colourPicker;
	private IntBuffer pixelBuffer;
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
			addButton(new Button(30, topOffset, 40, 20, new TranslationTextComponent("gui.button.edit"), b -> handleTintButton(finalId)));
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new TextFieldWidget(font, 30, editPaneTop + 20, 73, 10, null);
		hexText.setMaxStringLength(6);
		hexText.setText(Integer.toHexString(currTintColour));
		addListener(hexText);

		// RGB sliders
		rgbSliders = new HSBSlider[3];
		rgbSliders[0] = new HSBSlider(5, 5, editPaneTop + 70, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("gui.slider.red.tooltip"));
		rgbSliders[1] = new HSBSlider(6, 5, editPaneTop + 80, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("gui.slider.green.tooltip"));
		rgbSliders[2] = new HSBSlider(7, 5, editPaneTop + 90, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("gui.slider.blue.tooltip"));
		rgbSliders[0].setHue(0);
		rgbSliders[1].setHue(1F / 3F);
		rgbSliders[2].setHue(2F / 3F);

		addButton(rgbSliders[0]);
		addButton(rgbSliders[1]);
		addButton(rgbSliders[2]);

		// HBS sliders
		hsbSliders = new HSBSlider[3];
		hsbSliders[0] = new HSBSlider(15, 5, editPaneTop + 35, 100, 10, this, HSBSlider.HSBSliderType.HUE, new TranslationTextComponent("gui.slider.hue.tooltip"));
		hsbSliders[1] = new HSBSlider(16, 5, editPaneTop + 45, 100, 10, this, HSBSlider.HSBSliderType.SATURATION, new TranslationTextComponent("gui.slider.saturation.tooltip"));
		hsbSliders[2] = new HSBSlider(17, 5, editPaneTop + 55, 100, 10, this, HSBSlider.HSBSliderType.BRIGHTNESS, new TranslationTextComponent("gui.slider.brightness.tooltip"));

		addButton(hsbSliders[0]);
		addButton(hsbSliders[1]);
		addButton(hsbSliders[2]);

		// Reset/Save
		addButton(tintReset = new IconButton(width - 20, editPaneTop + 2, IconButton.Icons.UNDO, b -> {
			currTintColour = parent.getOriginalPartInfo().tints[currTintEdit - 1] & 0xFFFFFF; // Ignore the alpha bits.
			hexText.setText(Integer.toHexString(currTintColour));
			refreshTintPane();
			tintReset.active = false;
		}, new TranslationTextComponent("gui.button.reset")));
		tintReset.active = false;

		// Color Picker
		addButton(colourPicker = new IconButton(width - 36, editPaneTop + 1, IconButton.Icons.EYEDROPPER, b -> setSelectingColour(true), new TranslationTextComponent("gui.button.picker.0"), new TranslationTextComponent("gui.button.picker.1")));
		colourPicker.visible = false;

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
			final int colour = parent.getEditingPartInfo().tints[tint - 1] | 0xFF << 24;
			fillGradient(matrixStack, 5, topOffset + 10, 25, topOffset + 30, colour, colour);
			font.drawString(matrixStack, I18n.format("gui.tint", tint), 5, topOffset, 0xFFFFFF);
			topOffset += 35;
		}

		// Editing tint pane
		if (currTintEdit > 0) {
			hLine(matrixStack, 0, width, editPaneTop, 0xFF000000);
			font.drawString(matrixStack, I18n.format("gui.tint.edit", currTintEdit), 5, editPaneTop + 5, 0xFFFFFF);

			font.drawString(matrixStack, I18n.format("gui.hex") + ":", 5, editPaneTop + 21, 0xFFFFFF);
			hexText.render(matrixStack, mouseX, mouseY, partialTicks);
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	protected void handleTintButton(int id) {
		currTintEdit = id - 1;
		currTintColour = parent.getEditingPartInfo().tints[currTintEdit - 1] & 0xFFFFFF; // Ignore the alpha bits.
		hexText.setText(Integer.toHexString(currTintColour));
		refreshTintPane();
		tintReset.active = false;
		colourPicker.active = true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (hexText.keyPressed(keyCode, scanCode, modifiers)) {
			try {
				// Gets the current color from the hex text.
				if (!Strings.isNullOrEmpty(hexText.getText()))
					currTintColour = Integer.parseInt(hexText.getText(), 16);
			} catch (NumberFormatException ignored) {}
			return true;
		}

		refreshTintPane();

		if (keyCode == GLFW.GLFW_KEY_ESCAPE && selectingColour) {
			setSelectingColour(false);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (selectingColour && mouseButton == 0) {
			currTintColour = getColourAtPoint(mouseX, mouseY); // Ignore alpha.
			setSelectingColour(false);
			refreshTintPane();
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onValueChangeHSBSlider(HSBSlider source, double sliderValue) {
		if (source == rgbSliders[0] || source == rgbSliders[1] || source == rgbSliders[2])
			currTintColour = new Color(
					(int) MathHelper.clamp(rgbSliders[0].getValue() * 255F, 0, 255),
					(int) MathHelper.clamp(rgbSliders[1].getValue() * 255F, 0, 255),
					(int) MathHelper.clamp(rgbSliders[2].getValue() * 255F, 0, 255)).getRGB();
		else {
			final float[] hsbvals = {(float) hsbSliders[0].getValue(), (float) hsbSliders[1].getValue(), (float) hsbSliders[2].getValue()};
			hsbvals[source.getType().ordinal()] = (float) sliderValue;
			currTintColour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
		}
		hexText.setText(Integer.toHexString(currTintColour));
		refreshTintPane();
	}

	private int getColourAtPoint(double x, double y) {
		int[] pixelData;
		final int pixels = 1;

		if (pixelBuffer == null)
			pixelBuffer = BufferUtils.createIntBuffer(pixels);
		pixelData = new int[pixels];

		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		pixelBuffer.clear();

		GL11.glReadPixels((int) x, (int) y, 1, 1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

		pixelBuffer.get(pixelData);

		return pixelData[0] & 0xFFFFFF;
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
		hexText.setTextColor(currTintColour);

		// RGB Sliders
		final Color c = new Color(currTintColour);
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

		if (currTintEdit > 0) {
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

		if (currTintEdit > 0) parent.getEditingPartInfo().tints[currTintEdit - 1] = currTintColour | 0xFF << 24; // Add the alpha manually.
		parent.setPartsInfo(parent.getEditingPartInfo());
	}

	public void setEditingTint(int value) {
		currTintEdit = value;
	}

	public int getEditingTint() {
		return currTintEdit;
	}
}
