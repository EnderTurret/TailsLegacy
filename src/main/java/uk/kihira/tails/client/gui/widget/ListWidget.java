/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

import uk.kihira.tails.client.RenderHelper;

/**
 * A version of {@link ObjectSelectionList} that improves upon some things.
 *
 * @param <T> The list type.
 */
public class ListWidget<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

	public ListWidget(int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
		super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
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
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		RenderHelper.startGlScissor(x0, y0, width, height);
		try {
			super.render(poseStack, mouseX, mouseY, partialTick);
		} catch (IndexOutOfBoundsException e) {
			// Thanks Mojang.
		}
		RenderHelper.endGlScissor();
	}

	@Override
	protected void renderBackground(PoseStack poseStack) {}

	@Override
	public int getRowWidth() {
		return width;
	}

	@Override
	protected int getScrollbarPosition() {
		return x1 - 6;
	}

	@Override
	protected void renderSelection(PoseStack poseStack, int top, int width, int height, int outerColor, int innerColor) {
		final int left = x0 + (this.width - width) / 2;
		int right = x0 + (this.width + width) / 2;
		if (getMaxScroll() > 0)
			right -= 6;
		fill(poseStack, left, top - 2, right, top + height + 2, outerColor);
		fill(poseStack, left + 1, top - 1, right - 1, top + height + 1, innerColor);
	}

	// Exposes isSelectedItem(), don't remove this.
	@Override
	public boolean isSelectedItem(int index) {
		return super.isSelectedItem(index);
	}

	public int getItemHeight() {
		return itemHeight;
	}
}
