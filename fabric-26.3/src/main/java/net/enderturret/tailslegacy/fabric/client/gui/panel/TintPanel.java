/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.gui.panel;

import java.awt.Color;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.sdl.SDLKeycode;
import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.system.MemoryUtil;

import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.fabric.client.RenderHelper;
import net.enderturret.tailslegacy.fabric.client.gui.EditorScreen;
import net.enderturret.tailslegacy.fabric.client.gui.TailsComponents;
import net.enderturret.tailslegacy.fabric.client.gui.widget.HSBSlider;
import net.enderturret.tailslegacy.fabric.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.fabric.client.gui.widget.SaturationSlider;

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
			addRenderableWidget(Button.builder(TailsComponents.EDIT_TINT, _ -> handleTintButton(finalId))
					.bounds(left + 30, topOffset, 40, 20)
					.build());
			topOffset += 35;
		}

		// Tint edit pane
		hexText = new EditBox(parent.font(), left + 30, editPaneTop + 20, 73, 10, Component.empty());
		hexText.setMaxLength(6);
		hexText.setResponder(this::parseHex);
		addRenderableWidget(hexText);

		// HSB sliders
		hue = new HSBSlider(left + 5, editPaneTop + 35, this, HSBSlider.HSBSliderType.HUE);
		hue.setTooltip(Tooltip.create(TailsComponents.HUE));
		saturation = new SaturationSlider(left + 5, editPaneTop + 45, this);
		saturation.setTooltip(Tooltip.create(TailsComponents.SATURATION));
		brightness = new HSBSlider(left + 5, editPaneTop + 55, this, HSBSlider.HSBSliderType.BRIGHTNESS);
		brightness.setTooltip(Tooltip.create(TailsComponents.BRIGHTNESS));

		addRenderableWidget(hue);
		addRenderableWidget(saturation);
		addRenderableWidget(brightness);

		// RGB sliders
		red = new SaturationSlider(left + 5, editPaneTop + 70, this);
		green = new SaturationSlider(left + 5, editPaneTop + 80, this);
		blue = new SaturationSlider(left + 5, editPaneTop + 90, this);
		red.setTooltip(Tooltip.create(TailsComponents.RED));
		red.setHue(0);
		green.setTooltip(Tooltip.create(TailsComponents.GREEN));
		green.setHue(1F / 3F);
		blue.setTooltip(Tooltip.create(TailsComponents.BLUE));
		blue.setHue(2F / 3F);

		addRenderableWidget(red);
		addRenderableWidget(green);
		addRenderableWidget(blue);

		// Reset/Save
		addRenderableWidget(tintReset = new IconButton(right - 20, editPaneTop + 2, TailsIcons.UNDO, _ -> {
			final int newTint = parent.getOriginalPartInfo().getTints()[editingTint - 1] & 0xFFFFFF; // Ignore the alpha bits.
			refreshTintPane(newTint, true);
			tintReset.active = false;
		}));
		tintReset.setTooltip(Tooltip.create(TailsComponents.RESET_TINT));
		tintReset.active = false;

		// Color Picker
		addRenderableWidget(colourPicker = new IconButton(right - 36, editPaneTop + 1, TailsIcons.EYEDROPPER, _ -> setSelectingColour(true)));
		colourPicker.setTooltip(Tooltip.create(TailsComponents.COLOR_PICKER));
		colourPicker.visible = false;

		refreshTintPane(currentTint, true, true);
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractWidgetRenderState(gui, mouseX, mouseY, partialTick);

		// Tints
		int topOffset = top + 10;
		for (int tint = 1; tint <= 3; tint++) {
			final int colour = parent.getEditingPartInfo().getTints()[tint - 1] | 0xFF << 24;
			gui.fillGradient(left + 5, topOffset + 10, left + 25, topOffset + 30, colour, colour);
			gui.text(parent.font(), I18n.get(TailsLanguage.TINT_LABEL, tint), left + 5, topOffset, 0xFFFFFFFF);
			topOffset += 35;
		}

		// Editing tint pane
		if (editingTint > 0) {
			gui.horizontalLine(left, right, editPaneTop, 0xFF000000);
			gui.text(parent.font(), I18n.get(TailsLanguage.EDITING_TINT, editingTint), left + 5, editPaneTop + 5, 0xFFFFFFFF);

			gui.text(parent.font(), TailsComponents.HEX, left + 5, editPaneTop + 21, 0xFFFFFFFF);
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
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == SDLKeycode.SDLK_ESCAPE && selectingColour) {
			setSelectingColour(false);
			return true;
		}

		return super.keyPressed(event);
	}

	private void parseHex(String text) {
		try {
			if (!Strings.isNullOrEmpty(text))
				refreshTintPane(Integer.parseInt(text, 16), false);
		} catch (NumberFormatException ignored) {}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		if (selectingColour && event.button() == 1) {
			RenderHelper.getColourAtPoint(event.x(), event.y(), newTint -> refreshTintPane(newTint, true));

			setSelectingColour(false);

			return true;
		}

		return super.mouseClicked(event, isDoubleClick);
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

		SDLMouse.SDL_SetCursor(cursor);
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

		hexText.setTextColor(0xFF000000 | currentTint);
		if (changeText)
			hexText.setValue(JavaColor.hex(currentTint, true));

		// RGB Sliders
		final int red = JavaColor.red(currentTint), green = JavaColor.green(currentTint), blue = JavaColor.blue(currentTint);
		this.red.setValueWithoutCallback(red / 255F);
		this.green.setValueWithoutCallback(green / 255F);
		this.blue.setValueWithoutCallback(blue / 255F);

		// HSB Sliders
		final float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		hue.setValueWithoutCallback(hsbvals[0]);
		saturation.setValueWithoutCallback(hsbvals[1]);
		brightness.setValueWithoutCallback(hsbvals[2]);
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