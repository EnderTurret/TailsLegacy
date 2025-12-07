/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.panel;

import java.awt.Color;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common.JavaColor;
import uk.kihira.tails.common.TailsLanguage;
import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.forge.client.RenderHelper;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;
import uk.kihira.tails.forge.client.gui.widget.HSBSlider;
import uk.kihira.tails.forge.client.gui.widget.IconButton;
import uk.kihira.tails.forge.client.gui.widget.SaturationSlider;

@Internal
public final class TintPanel extends Panel implements HSBSlider.IHSBSliderCallback {

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

	public TintPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		editPaneTop = height - 107;
		// Edit tint buttons
		int topOffset = 20;
		for (int id = 1; id <= 3; id++) {
			final int finalId = id;
			addRenderableWidget(new ExtendedButton(left + 30, topOffset, 40, 20, TailsComponents.EDIT_TINT, b -> handleTintButton(finalId)));
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new EditBox(parent.font(), left + 31, editPaneTop + 21, 71, 8, Component.empty());
		hexText.setMaxLength(6);
		hexText.setResponder(this::parseHex);
		addRenderableWidget(hexText);

		// HSB sliders
		hue = new HSBSlider(left + 5, editPaneTop + 35, this, HSBSlider.HSBSliderType.HUE);
		hue.setTooltip(parent, TailsComponents.HUE);
		saturation = new SaturationSlider(left + 5, editPaneTop + 45, this);
		saturation.setTooltip(parent, TailsComponents.SATURATION);
		brightness = new HSBSlider(left + 5, editPaneTop + 55, this, HSBSlider.HSBSliderType.BRIGHTNESS);
		brightness.setTooltip(parent, TailsComponents.BRIGHTNESS);

		addRenderableWidget(hue);
		addRenderableWidget(saturation);
		addRenderableWidget(brightness);

		// RGB sliders
		red = new SaturationSlider(left + 5, editPaneTop + 70, this);
		green = new SaturationSlider(left + 5, editPaneTop + 80, this);
		blue = new SaturationSlider(left + 5, editPaneTop + 90, this);
		red.setTooltip(parent, TailsComponents.RED);
		red.setHue(0);
		green.setTooltip(parent, TailsComponents.GREEN);
		green.setHue(1F / 3F);
		blue.setTooltip(parent, TailsComponents.BLUE);
		blue.setHue(2F / 3F);

		addRenderableWidget(red);
		addRenderableWidget(green);
		addRenderableWidget(blue);

		// Reset/Save
		addRenderableWidget(tintReset = new IconButton(right - 20, editPaneTop + 2, TailsIcons.UNDO, b -> {
			final int newTint = parent.getOriginalPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
			refreshTintPane(newTint, true);
			tintReset.active = false;
		}));
		tintReset.setTooltip(parent, TailsComponents.RESET_TINT);
		tintReset.active = false;

		// Color Picker
		addRenderableWidget(colourPicker = new IconButton(right - 36, editPaneTop + 1, TailsIcons.EYEDROPPER, b -> setSelectingColour(true)));
		colourPicker.setTooltip(parent, TailsComponents.COLOR_PICKER);
		colourPicker.visible = false;

		refreshTintPane(currentTint, true, true);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		super.renderButton(poseStack, mouseX, mouseY, partialTick);

		// Tints
		int topOffset = top + 10;
		for (int tint = 1; tint <= 3; tint++) {
			final int colour = parent.getEditingPartInfo().getTints()[tint - 1] | 0xFF << 24;
			fill(poseStack, left + 5, topOffset + 10, left + 25, topOffset + 30, colour);
			drawString(poseStack, parent.font(), I18n.get(TailsLanguage.TINT_LABEL, tint), left + 5, topOffset, 0xFFFFFF);
			topOffset += 35;
		}

		// Editing tint pane
		if (editingTint > 0) {
			hLine(poseStack, left, right, editPaneTop, 0xFF000000);
			drawString(poseStack, parent.font(), I18n.get(TailsLanguage.EDITING_TINT, editingTint), left + 5, editPaneTop + 5, 0xFFFFFF);

			drawString(poseStack, parent.font(), TailsComponents.HEX, left + 5, editPaneTop + 21, 0xFFFFFF);
		}
	}

	protected void handleTintButton(int id) {
		editingTint = id;
		final int newTint = parent.getEditingPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
		refreshTintPane(newTint, true, true);
		tintReset.active = false;
		//colourPicker.active = true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && selectingColour) {
			setSelectingColour(false);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void parseHex(String text) {
		try {
			if (!Strings.isNullOrEmpty(text))
				refreshTintPane(Integer.parseInt(text, 16), false);
		} catch (NumberFormatException ignored) {}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (selectingColour && mouseButton == 0) {
			final int newTint = RenderHelper.getColourAtPoint(mouseX, mouseY);

			setSelectingColour(false);
			refreshTintPane(newTint, true);

			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onValueChangeHSBSlider(HSBSlider source, double sliderValue) {
		final int newTint;

		if (source == red || source == green || source == blue)
			newTint = JavaColor.pack(
					(int) TailsMath.clamp(red.getValue() * 255F, 0, 255),
					(int) TailsMath.clamp(green.getValue() * 255F, 0, 255),
					(int) TailsMath.clamp(blue.getValue() * 255F, 0, 255));

		else {
			final float[] hsbvals = { (float) hue.getValue(), (float) saturation.getValue(), (float) brightness.getValue() };
			hsbvals[source.getType().ordinal()] = (float) sliderValue;
			newTint = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
		}

		refreshTintPane(newTint, true);
	}

	private void setSelectingColour(boolean selectingColour) {
		this.selectingColour = selectingColour;

		final long cursor = selectingColour ? pickerCursorHandle : MemoryUtil.NULL;

		GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), cursor);
	}

	public boolean isSelectingColour() {
		return selectingColour;
	}

	@Override
	public void setVisible(boolean value) {
		super.setVisible(value);

		final boolean visible = editingTint > 0;

		red.visible = green.visible = blue.visible =
				hue.visible = saturation.visible = brightness.visible =
				tintReset.visible = colourPicker.visible = hexText.visible = visible;
	}

	public void refreshTintPane(int newTint, boolean changeText) {
		refreshTintPane(newTint, changeText, false);
	}

	public void refreshTintPane(int newTint, boolean changeText, boolean force) {
		final boolean visible = editingTint > 0;

		red.visible = green.visible = blue.visible =
				hue.visible = saturation.visible = brightness.visible =
				tintReset.visible = colourPicker.visible = hexText.visible = visible;

		if (!force && newTint == currentTint) return;

		currentTint = newTint;

		hexText.setTextColor(currentTint);
		if (changeText)
			hexText.setValue(JavaColor.hex(currentTint, true));

		// RGB Sliders
		final int red = JavaColor.red(currentTint), green = JavaColor.green(currentTint), blue = JavaColor.blue(currentTint);
		this.red.setValue(red / 255F);
		this.green.setValue(green / 255F);
		this.blue.setValue(blue / 255F);

		// HSB Sliders
		final float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		hue.setValue(hsbvals[0]);
		saturation.setValue(hsbvals[1]);
		brightness.setValue(hsbvals[2]);
		// The saturation slider needs to know the value of the other 2 sliders.
		saturation.setHue((float) hue.getValue());
		saturation.setBrightness((float) brightness.getValue());

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