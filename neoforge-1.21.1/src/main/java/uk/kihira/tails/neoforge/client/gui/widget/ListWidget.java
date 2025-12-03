/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.widget;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

	public void onItemSelected(T item) {}

	@Override
	public void setSelected(T selected) {
		final boolean changed = getSelected() != selected;

		super.setSelected(selected);

		if (changed) onItemSelected(selected);
	}

	@Override
	protected void renderListBackground(GuiGraphics gui) {}

	@Override
	protected void renderListSeparators(GuiGraphics guiGraphics) {}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return visible && super.isMouseOver(mouseX, mouseY);
	}

	@Override
	public int getRowWidth() {
		return width;
	}

	@Override
	protected int getScrollbarPosition() {
		return getRowRight() - 8;
	}

	@Override
	protected void renderSelection(GuiGraphics gui, int top, int width, int height, int outerColor, int innerColor) {
		final int left = getX() + (this.width - width) / 2;
		int right = getX() + (this.width + width) / 2;
		if (getMaxScroll() > 0)
			right -= 6;
		gui.fill(left, top - 2, right, top + height + 2, outerColor);
		gui.fill(left + 1, top - 1, right - 1, top + height + 1, innerColor);
	}

	// Exposes isSelectedItem(), don't remove this.
	@Override
	public boolean isSelectedItem(int index) {
		return super.isSelectedItem(index);
	}

	@Override
	public void replaceEntries(Collection<T> entries) {
		super.replaceEntries(entries);
	}

	public int getItemHeight() {
		return itemHeight;
	}
}
