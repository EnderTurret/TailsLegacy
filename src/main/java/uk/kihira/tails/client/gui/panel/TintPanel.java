/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
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
import uk.kihira.tails.client.gui.widget.SaturationSlider;

@Internal
public final class TintPanel extends Panel<EditorScreen> implements HSBSlider.IHSBSliderCallback {

	private int editingTint = 0;
	private int currentTint = 0xFFFFFF;
	private EditBox hexText;

	private HSBSlider hue;
	private SaturationSlider saturation;
	private HSBSlider brightness;

	private SaturationSlider red;
	private SaturationSlider green;
	private SaturationSlider blue;

	private IconButton tintReset;
	private IconButton colourPicker;
	private boolean selectingColour = false;
	private int editPaneTop;

	@Internal
	public static long pickerCursorHandle = MemoryUtil.NULL;

	public TintPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
		alwaysReceiveMouse = true;
	}

	@Override
	public void init() {
		editPaneTop = height - 107;
		// Edit tint buttons
		int topOffset = 20;
		for (int id = 1; id <= 3; id++) {
			final int finalId = id;
			addRenderableWidget(new Button(30, topOffset, 40, 20, Component.translatable("tails.gui.button.edit"), b -> handleTintButton(finalId)));
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new RelativeTextBox(this, font, 30, editPaneTop + 20, 73, 10, null);
		hexText.setMaxLength(6);
		addRenderableWidget(hexText);

		// RGB sliders
		red = new SaturationSlider(5, editPaneTop + 70, 100, 10, this, Component.translatable("tails.gui.slider.red.tooltip"));
		green = new SaturationSlider(5, editPaneTop + 80, 100, 10, this, Component.translatable("tails.gui.slider.green.tooltip"));
		blue = new SaturationSlider(5, editPaneTop + 90, 100, 10, this, Component.translatable("tails.gui.slider.blue.tooltip"));
		red.setHue(0);
		green.setHue(1F / 3F);
		blue.setHue(2F / 3F);

		addRenderableWidget(red);
		addRenderableWidget(green);
		addRenderableWidget(blue);

		// HSB sliders
		hue = new HSBSlider(5, editPaneTop + 35, 100, 10, this, HSBSlider.HSBSliderType.HUE, Component.translatable("tails.gui.slider.hue.tooltip"));
		saturation = new SaturationSlider(5, editPaneTop + 45, 100, 10, this, Component.translatable("tails.gui.slider.saturation.tooltip"));
		brightness = new HSBSlider(5, editPaneTop + 55, 100, 10, this, HSBSlider.HSBSliderType.BRIGHTNESS, Component.translatable("tails.gui.slider.brightness.tooltip"));

		addRenderableWidget(hue);
		addRenderableWidget(saturation);
		addRenderableWidget(brightness);

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
		}

		super.render(poseStack, mouseX, mouseY, partialTick);
	}

	protected void handleTintButton(int id) {
		editingTint = id;
		final int newTint = parent.getEditingPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
		refreshTintPane(newTint);
		tintReset.active = false;
		//colourPicker.active = true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (hexText.keyPressed(keyCode, scanCode, modifiers)) {
			parseHex();
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
			parseHex();
			return true;
		}

		return super.charTyped(codePoint, modifiers);
	}

	private void parseHex() {
		try {
			if (!Strings.isNullOrEmpty(hexText.getValue()))
				refreshTintPane(Integer.parseInt(hexText.getValue(), 16));
		} catch (NumberFormatException ignored) {}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (selectingColour && mouseButton == 0) {
			// Mouse coordinates are relative to the panel, so we need to resolve them to screen coordinates.
			final int newTint = getColourAtPoint(mouseX + left, mouseY + top);

			setSelectingColour(false);
			refreshTintPane(newTint);

			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onValueChangeHSBSlider(HSBSlider source, double sliderValue) {
		final int newTint;

		if (source == red || source == green || source == blue)
			newTint = new Color(
					(int) Mth.clamp(red.getValue() * 255F, 0, 255),
					(int) Mth.clamp(green.getValue() * 255F, 0, 255),
					(int) Mth.clamp(blue.getValue() * 255F, 0, 255)).getRGB();

		else {
			final float[] hsbvals = {(float) hue.getValue(), (float) saturation.getValue(), (float) brightness.getValue()};
			hsbvals[source.getType().ordinal()] = (float) sliderValue;
			newTint = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
		}

		refreshTintPane(newTint);
	}

	private static int getColourAtPoint(double x, double y) {
		final Minecraft mc = Minecraft.getInstance();

		// We have to resolve these mouse coordinates back to window coordinates.
		final double scale = mc.getWindow().getGuiScale();
		x *= scale;
		y *= scale;

		// We also have to flip the y coordinate because OpenGL's
		// coordinate system is upside-down compared to ours.
		y = mc.getWindow().getHeight() - y;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			final ByteBuffer pixelBuffer = stack.calloc(3);

			GL11.glReadBuffer(GL11.GL_FRONT);

			RenderSystem.pixelStore(GL11.GL_PACK_ALIGNMENT, 1);
			RenderSystem.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 1);

			RenderSystem.readPixels((int) x, (int) y, 1, 1,
					GL11.GL_RGB,
					GL11.GL_UNSIGNED_BYTE,
					pixelBuffer);

			pixelBuffer.rewind();

			final int r = pixelBuffer.get() & 0xFF;
			final int g = pixelBuffer.get() & 0xFF;
			final int b = pixelBuffer.get() & 0xFF;

			return (r << 16) | (g << 8) | b;
		}
	}

	private void setSelectingColour(boolean selectingColour) {
		this.selectingColour = selectingColour;

		final long cursor = selectingColour ? pickerCursorHandle : MemoryUtil.NULL;

		GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), cursor);
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
		red.setValue(c.getRed() / 255F);
		green.setValue(c.getGreen() / 255F);
		blue.setValue(c.getBlue() / 255F);

		// HSB Sliders
		final float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hue.setValue(hsbvals[0]);
		saturation.setValue(hsbvals[1]);
		brightness.setValue(hsbvals[2]);
		// The saturation slider needs to know the value of the other 2 sliders.
		saturation.setHue((float) hue.getValue());
		saturation.setBrightness((float) brightness.getValue());

		final boolean visible = editingTint > 0;

		red.visible = green.visible = blue.visible =
				hue.visible = saturation.visible = brightness.visible =
				tintReset.visible = colourPicker.visible = hexText.visible = visible;

		tintReset.active = true;

		if (editingTint > 0)
			parent.getEditingPartInfo().getTints()[editingTint - 1] = currentTint | 0xFF000000; // Add the alpha manually.

		parent.setPartsInfo(parent.getEditingPartInfo(), true);
	}

	public void setEditingTint(int value) {
		editingTint = value;
		refreshTintPane(currentTint, true);
	}

	public int getEditingTint() {
		return editingTint;
	}
}