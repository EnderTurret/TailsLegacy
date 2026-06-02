/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

import net.enderturret.tailslegacy.forge.client.RenderHelper;

/**
 * A version of {@link ObjectSelectionList} that improves upon some things.
 *
 * @param <T> The list type.
 */
public class ListWidget<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

	public boolean visible = true;

	public ListWidget(int width, int height, int top, int slotHeight, List<T> entries) {
		super(Minecraft.getInstance(), width, height, top, top + height, slotHeight);
		replaceEntries(entries);
		setRenderTopAndBottom(false);
		setRenderBackground(false);
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
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (visible) {
			RenderHelper.startGlScissor(x0, y0, x1, y1);

			super.render(poseStack, mouseX, mouseY, partialTick);

			RenderHelper.endGlScissor();
		}
	}

	@Override
	protected void renderBackground(PoseStack poseStack) {}

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

	@Override
	public void replaceEntries(Collection<T> entries) {
		super.replaceEntries(entries);
	}

	public int getItemHeight() {
		return itemHeight;
	}
}
