/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;

/**
 * A version of {@link ObjectSelectionList} that improves upon some things.
 *
 * @param <T> The list type.
 */
public class ListWidget<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

	public ListWidget(int width, int height, int top, int slotHeight, List<T> entries) {
		super(Minecraft.getInstance(), width, height, top, slotHeight);
		replaceEntries(entries);
	}

	public ListWidget(int width, int height, int top, int slotHeight) {
		this(width, height, top, slotHeight, new ArrayList<>());
	}

	public void onItemSelected(T item) {}

	@Override
	public void setSelected(T selected) {
		final boolean changed = getSelected() != selected;

		super.setSelected(selected);

		if (changed) onItemSelected(selected);
	}

	@Override
	protected void extractListBackground(GuiGraphicsExtractor gui) {}

	@Override
	protected void extractListSeparators(GuiGraphicsExtractor guiGraphics) {}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return visible && super.isMouseOver(mouseX, mouseY);
	}

	@Override
	public int getRowWidth() {
		return width;
	}

	@Override
	protected int scrollBarX() {
		return getRowRight() - 6;
	}

	@Override
	protected void extractSelection(GuiGraphicsExtractor gui, T entry, int backgroundColor) {
		final int left = entry.getX();
		int right = left + entry.getWidth();
		final int top = entry.getY();
		final int bottom = top + entry.getHeight();
		if (maxScrollAmount() > 0)
			right -= 6;
		gui.fill(left, top, right, bottom, backgroundColor);
		gui.fill(left + 1, top + 1, right - 1, bottom - 1, -16777216);
	}

	@Override
	public void replaceEntries(Collection<T> entries) {
		super.replaceEntries(entries);
		setScrollAmount(0);
	}

	@Override
	public int addEntry(T entry) {
		return super.addEntry(entry);
	}

	@Override
	public void removeEntry(T entry) {
		super.removeEntry(entry);
	}

	public int getItemHeight() {
		return defaultEntryHeight;
	}
}
