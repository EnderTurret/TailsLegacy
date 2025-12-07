/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.widget;

import java.util.NavigableSet;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common.client.gui.BaseSpinner;
import uk.kihira.tails.forge.client.RenderHelper;

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

		left = new ExtendedButton(x, y, 15, 15, Component.literal("<"), b -> previous());
		right = new ExtendedButton(0, y, 15, 15, Component.literal(">"), b -> next());

		setHeight(Math.max(left.getHeight(), Minecraft.getInstance().font.lineHeight));

		setValues(Objects.requireNonNull(values));
		select(initialSelection, false);

		final int off = getWidth() / 2;

		x = centerX - off;
		left.x = x;
		right.x = x + getWidth() - right.getWidth();
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
	public boolean changeFocus(boolean focus) {
		return false;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		final Font font = Minecraft.getInstance().font;
		final Component message = getMessage();
		final int width = font.width(message);

		int left = x;
		left += getWidth() / 2;
		left -= width / 2;

		final int y = this.y + (getHeight() - font.lineHeight) / 2;

		if (width > x + getWidth())
			RenderHelper.drawScrollingString(poseStack, font, message, x + 15, x + getWidth(), y, 0xFFFFFFFF);
		else
			GuiComponent.drawString(poseStack, font, message, left, y, 0xFFFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
	}
}