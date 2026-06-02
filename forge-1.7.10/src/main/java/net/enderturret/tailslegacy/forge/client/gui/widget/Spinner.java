/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.NavigableSet;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import net.enderturret.tailslegacy.common.client.gui.BaseSpinner;
import net.enderturret.tailslegacy.forge.client.RenderHelper;

import cpw.mods.fml.client.config.GuiButtonExt;

/**
 * A widget that allows cycling through values using two arrow buttons.
 * It is somewhat comparable to Java's spinners in design, but behaves closer to a combo box.
 * @author EnderTurret
 *
 * @param <T> The type of the elements the spinner cycles through.
 */
public class Spinner<T> extends GuiButton implements BaseSpinner<T> {

	public final GuiButtonExt left;
	public final GuiButtonExt right;

	private NavigableSet<T> values;
	private T selected;

	private final Stringifier<T> stringifier;
	private final Listener<T> listener;

	public Spinner(int id, NavigableSet<T> values, @Nullable T initialSelection, int centerX, int y, int width, Stringifier<T> stringifier, Listener<T> listener) {
		super(id, centerX, y, width, 0, "");
		this.stringifier = Objects.requireNonNull(stringifier);
		this.listener = Objects.requireNonNull(listener);

		left = new GuiButtonExt(id + 1, xPosition, y, 15, 15, "<");
		right = new GuiButtonExt(id + 2, 0, y, 15, 15, ">");

		height = Math.max(left.height, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);

		setValues(Objects.requireNonNull(values));
		select(initialSelection, false);

		final int off = width / 2;

		xPosition = centerX - off;
		left.xPosition = xPosition;
		right.xPosition = xPosition + this.width - right.width;
	}

	public Spinner(int id, NavigableSet<T> values, int x, int y, int width, Stringifier<T> stringifier, Listener<T> listener) {
		this(id, values, null, x, y, width, stringifier, listener);
	}

	@Override
	public void setValues(NavigableSet<T> values) {
		final boolean runCallback = values == null;

		this.values = values;
		select(values.first(), runCallback);
	}

	@Override
	public NavigableSet<T> values() {
		return values;
	}

	@Override
	public T getSelection() {
		return selected;
	}

	@Override
	public T select(T value) {
		return select(value, true);
	}

	private T select(T value, boolean runCallback) {
		selected = Objects.requireNonNull(value);
		displayString = I18n.format(stringifier.stringify(value));
		if (runCallback) listener.onSelected(value);
		return value;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible) return;

		final FontRenderer font = mc.fontRenderer;
		final String message = displayString;
		final int width = font.getStringWidth(message);

		int left = xPosition;
		left += this.width / 2;
		left -= width / 2;

		final int y = this.yPosition + (height - font.FONT_HEIGHT) / 2;

		if (width > xPosition + this.width)
			RenderHelper.drawScrollingString(font, message, xPosition + 15, xPosition + this.width, y, 0xFFFFFFFF);
		else
			font.drawString(message, left, y, 0xFFFFFFFF);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return false;
	}
}