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
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.client.config.GuiButtonExt;

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
import uk.kihira.tails.forge.client.gui.widget.SimpleGuiTextField;

@Internal
public final class TintPanel extends Panel implements HSBSlider.IHSBSliderCallback {

	public static final int TINT_1 = 800;
	public static final int TINT_2 = 801;
	public static final int TINT_3 = 802;
	public static final int HEX = 803;
	public static final int HUE = 804;
	public static final int SATURATION = 805;
	public static final int BRIGHTNESS = 806;
	public static final int RED = 807;
	public static final int GREEN = 808;
	public static final int BLUE = 809;
	public static final int RESET_TINT = 810;
	public static final int COLOR_PICKER = 811;

	private int editingTint = 0;
	private int currentTint = 0xFFFFFF;
	private SimpleGuiTextField hexText;

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
	public static Cursor pickerCursorHandle;

	public TintPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		editPaneTop = bottom - 107;
		// Edit tint buttons
		int topOffset = 20;
		for (int id = 1; id <= 3; id++) {
			final int finalId = id;
			addRenderableWidget(new GuiButtonExt(TINT_1 + id - 1, left + 30, topOffset, 40, 20, TailsComponents.EDIT_TINT.getFormattedText()));
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new SimpleGuiTextField(HEX, parent.font(), left + 31, editPaneTop + 21, 71, 8);
		hexText.setMaxStringLength(6);
		hexText.setGuiResponder(this::parseHex);
		addRenderableWidget(hexText);

		// HSB sliders
		hue = new HSBSlider(HUE, left + 5, editPaneTop + 35, this, HSBSlider.HSBSliderType.HUE);
		hue.setTooltip(parent, TailsComponents.HUE.getFormattedText());
		saturation = new SaturationSlider(SATURATION, left + 5, editPaneTop + 45, this);
		saturation.setTooltip(parent, TailsComponents.SATURATION.getFormattedText());
		brightness = new HSBSlider(BRIGHTNESS, left + 5, editPaneTop + 55, this, HSBSlider.HSBSliderType.BRIGHTNESS);
		brightness.setTooltip(parent, TailsComponents.BRIGHTNESS.getFormattedText());

		addRenderableWidget(hue);
		addRenderableWidget(saturation);
		addRenderableWidget(brightness);

		// RGB sliders
		red = new SaturationSlider(RED, left + 5, editPaneTop + 70, this);
		green = new SaturationSlider(GREEN, left + 5, editPaneTop + 80, this);
		blue = new SaturationSlider(BLUE, left + 5, editPaneTop + 90, this);
		red.setTooltip(parent, TailsComponents.RED.getFormattedText());
		red.setHue(0);
		green.setTooltip(parent, TailsComponents.GREEN.getFormattedText());
		green.setHue(1F / 3F);
		blue.setTooltip(parent, TailsComponents.BLUE.getFormattedText());
		blue.setHue(2F / 3F);

		addRenderableWidget(red);
		addRenderableWidget(green);
		addRenderableWidget(blue);

		// Reset/Save
		addRenderableWidget(tintReset = new IconButton(RESET_TINT, right - 20, editPaneTop + 2, TailsIcons.UNDO));
		tintReset.setTooltip(parent, TailsComponents.RESET_TINT.getFormattedText());
		tintReset.enabled = false;

		// Color Picker
		addRenderableWidget(colourPicker = new IconButton(COLOR_PICKER, right - 36, editPaneTop + 1, TailsIcons.EYEDROPPER));
		colourPicker.setTooltip(parent, TailsComponents.COLOR_PICKER.getFormattedText());
		colourPicker.visible = false;

		refreshTintPane(currentTint, true, true);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case TINT_1:
				handleTintButton(1); break;
			case TINT_2:
				handleTintButton(2); break;
			case TINT_3:
				handleTintButton(3); break;
			case RESET_TINT:
				final int newTint = parent.getOriginalPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
				refreshTintPane(newTint, true);
				tintReset.enabled = false;
				break;
			case COLOR_PICKER:
				setSelectingColour(true); break;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		// Tints
		int topOffset = top + 10;
		for (int tint = 1; tint <= 3; tint++) {
			final int colour = parent.getEditingPartInfo().getTints()[tint - 1] | 0xFF << 24;
			drawRect(left + 5, topOffset + 10, left + 25, topOffset + 30, colour);
			parent.font().drawString(I18n.format(TailsLanguage.TINT_LABEL, tint), left + 5, topOffset, 0xFFFFFF);
			topOffset += 35;
		}

		// Editing tint pane
		if (editingTint > 0) {
			drawHorizontalLine(left, right, editPaneTop, 0xFF000000);
			parent.font().drawString(I18n.format(TailsLanguage.EDITING_TINT, editingTint), left + 5, editPaneTop + 5, 0xFFFFFF);

			parent.font().drawString(TailsComponents.HEX.getFormattedText(), left + 5, editPaneTop + 21, 0xFFFFFF);
		}
	}

	protected void handleTintButton(int id) {
		editingTint = id;
		final int newTint = parent.getEditingPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
		refreshTintPane(newTint, true, true);
		tintReset.enabled = false;
		//colourPicker.enabled = true;
	}

	@Override
	public boolean keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1 && selectingColour) {
			setSelectingColour(false);
			return true;
		}

		return super.keyTyped(typedChar, keyCode);
	}

	private void parseHex(String text) {
		try {
			if (!Strings.isNullOrEmpty(text))
				refreshTintPane(Integer.parseInt(text, 16), false);
		} catch (NumberFormatException ignored) {}
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (selectingColour && mouseButton == 0) {
			final int newTint = RenderHelper.getColourAtPoint(mouseX, mouseY);

			setSelectingColour(false);
			refreshTintPane(newTint, true);

			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onValueChangeHSBSlider(int sourceId, double sliderValue) {
		int newTint = 0;
		HSBSlider source = null;

		switch (sourceId) {
			case RED:
			case GREEN:
			case BLUE:
				newTint = JavaColor.pack(
						(int) TailsMath.clamp(red.getSliderValue() * 255F, 0, 255),
						(int) TailsMath.clamp(green.getSliderValue() * 255F, 0, 255),
						(int) TailsMath.clamp(blue.getSliderValue() * 255F, 0, 255));
				break;
			case HUE:
				source = hue; break;
			case SATURATION:
				source = saturation; break;
			case BRIGHTNESS:
				source = brightness; break;
		}

		if (source != null) {
			final float[] hsbvals = { hue.getSliderValue(), saturation.getSliderValue(), brightness.getSliderValue() };
			hsbvals[source.getType().ordinal()] = (float) sliderValue;
			newTint = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
		}

		refreshTintPane(newTint, true);
	}

	private void setSelectingColour(boolean selectingColour) {
		this.selectingColour = selectingColour;

		final Cursor cursor = selectingColour ? pickerCursorHandle : null;

		try {
			Mouse.setNativeCursor(cursor);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
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
				tintReset.visible = colourPicker.visible = visible;
		hexText.setVisible(visible);
	}

	public void refreshTintPane(int newTint, boolean changeText) {
		refreshTintPane(newTint, changeText, false);
	}

	public void refreshTintPane(int newTint, boolean changeText, boolean force) {
		final boolean visible = editingTint > 0;

		red.visible = green.visible = blue.visible =
				hue.visible = saturation.visible = brightness.visible =
				tintReset.visible = colourPicker.visible = visible;
		hexText.setVisible(visible);

		if (!force && newTint == currentTint) return;

		currentTint = newTint;

		hexText.setTextColor(currentTint);
		if (changeText)
			hexText.setText(JavaColor.hex(currentTint, true));

		// RGB Sliders
		final int red = JavaColor.red(currentTint), green = JavaColor.green(currentTint), blue = JavaColor.blue(currentTint);
		this.red.setSliderValue(red / 255F, false);
		this.green.setSliderValue(green / 255F, false);
		this.blue.setSliderValue(blue / 255F, false);

		// HSB Sliders
		final float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		hue.setSliderValue(hsbvals[0], false);
		saturation.setSliderValue(hsbvals[1], false);
		brightness.setSliderValue(hsbvals[2], false);
		// The saturation slider needs to know the value of the other 2 sliders.
		saturation.setHue(hue.getSliderValue());
		saturation.setBrightness(brightness.getSliderValue());

		tintReset.enabled = true;

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