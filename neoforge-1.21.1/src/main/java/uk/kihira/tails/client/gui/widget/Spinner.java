/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import java.util.NavigableSet;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common2.client.gui.BaseSpinner;

/**
 * A widget that allows cycling through values using two arrow buttons.
 * It is somewhat comparable to Java's spinners in design, but behaves closer to a combo box.
 * @author EnderTurret
 *
 * @param <T> The type of the elements the spinner cycles through.
 */
public class Spinner<T> extends AbstractWidget implements BaseSpinner<T> {

	public final ExtendedButton left;
	public final ExtendedButton right;

	private NavigableSet<T> values;
	private T selected;

	private final Stringifier<T> stringifier;
	private final Listener<T> listener;

	public Spinner(NavigableSet<T> values, @Nullable T initialSelection, int centerX, int y, int width, Stringifier<T> stringifier, Listener<T> listener) {
		super(centerX, y, width, 0, Component.empty());
		this.stringifier = Objects.requireNonNull(stringifier);
		this.listener = Objects.requireNonNull(listener);

		left = new ExtendedButton(getX(), y, 15, 15, Component.literal("<"), b -> previous());
		right = new ExtendedButton(0, y, 15, 15, Component.literal(">"), b -> next());

		setHeight(Math.max(left.getHeight(), Minecraft.getInstance().font.lineHeight));

		setValues(Objects.requireNonNull(values));
		select(initialSelection, false);

		final int off = getWidth() / 2;

		setX(centerX - off);
		left.setX(getX());
		right.setX(getX() + getWidth() - right.getWidth());
	}

	public Spinner(NavigableSet<T> values, int x, int y, int width, Stringifier<T> stringifier, Listener<T> listener) {
		this(values, null, x, y, width, stringifier, listener);
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
		setMessage(Component.translatable(stringifier.stringify(value)));
		if (runCallback) listener.onSelected(value);
		return value;
	}

	@Override
	public ComponentPath nextFocusPath(FocusNavigationEvent event) {
		return null;
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		final Font font = Minecraft.getInstance().font;
		final Component message = getMessage();
		final int width = font.width(message);

		int left = getX();
		left += getWidth() / 2;
		left -= width / 2;

		gui.drawString(font, message, left, getY() + getHeight() /  2 - font.lineHeight / 2, 0xFFFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
	}
}