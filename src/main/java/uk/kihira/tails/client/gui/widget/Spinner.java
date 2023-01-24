package uk.kihira.tails.client.gui.widget;

import java.util.NavigableSet;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

public class Spinner<T> extends AbstractWidget {

	public int centerX;

	public final ExtendedButton left;
	public final ExtendedButton right;

	private NavigableSet<T> values;
	private T selected;

	private final Stringifier<T> stringifier;
	private final Listener<T> listener;

	public Spinner(NavigableSet<T> values, @Nullable T initialSelection, int centerX, int y, int width, Stringifier<T> stringifier, Listener<T> listener) {
		super(centerX, y, width, 0, Component.empty());
		this.centerX = centerX;
		this.stringifier = Objects.requireNonNull(stringifier);
		this.listener = Objects.requireNonNull(listener);

		left = new FocusableExtendedButton(x, y, 15, 15, Component.literal("<"), this::previous);
		right = new FocusableExtendedButton(0, y, 15, 15, Component.literal(">"), this::next);

		setHeight(Math.max(left.getHeight(), Minecraft.getInstance().font.lineHeight));

		setValues(Objects.requireNonNull(values));

		final int off = getWidth() / 2;

		x = centerX - off;
		left.x = x;
		right.x = x + getWidth() - right.getWidth();
	}

	public Spinner(NavigableSet<T> values, int x, int y, int width, Stringifier<T> stringifier, Listener<T> listener) {
		this(values, null, x, y, width, stringifier, listener);
	}

	public void setValues(NavigableSet<T> values) {
		final boolean runCallback = values == null;

		this.values = values;
		select(values.first(), runCallback);
	}

	public T getSelection() {
		return selected;
	}

	public T select(T value) {
		return select(value, true);
	}

	private T select(T value, boolean runCallback) {
		selected = Objects.requireNonNull(value);
		setMessage(stringifier.stringify(value));
		if (runCallback) listener.onSelected(value);
		return value;
	}

	private void next(Button b) {
		next();
	}

	public T next() {
		T next = values.higher(selected);
		return select(next == null ? values.first() : next);
	}

	private void previous(Button b) {
		previous();
	}

	public T previous() {
		T previous = values.lower(selected);
		return select(previous == null ? values.last() : previous);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		final Font font = Minecraft.getInstance().font;
		final Component message = getMessage();
		final int width = font.width(message);

		int left = x;
		left += getWidth() / 2;
		left -= width / 2;

		font.draw(poseStack, message, left, y + getHeight() /  2 - font.lineHeight / 2, 0xFFFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
	}

	@FunctionalInterface
	public static interface Stringifier<T> {
		public Component stringify(T value);
	}

	@FunctionalInterface
	public static interface Listener<T> {
		public void onSelected(T selection);
	}
}