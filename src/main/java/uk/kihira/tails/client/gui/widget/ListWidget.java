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
 * An extended version of the extended list.<br>
 * Could have been called {@code ExtendedExtendedList}.
 *
 * @param <T> The list type.
 */
public class ListWidget<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {

	public ListWidget(int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
		super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
		replaceEntries(entries);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		RenderHelper.startGlScissor(x0, y0, width + 3, height);
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
		return x1;
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
