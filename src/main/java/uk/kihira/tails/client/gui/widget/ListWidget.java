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

	// TODO: IListCallback isn't actually used for some reason. Nothing seems to be broken, and it looks like selection callbacks occur in the list elements, so consider removing?
	public ListWidget(IListCallback<T> parent, int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
		super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
		replaceEntries(entries);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderHelper.startGlScissor(x0, y0, width + 3, height);
		try {
			// TODO: Lists draw the dirt background. How do we want to handle this? Copy the code and draw black instead? Or adopt it in other panels?
			// Alternatively, we could just ignore it, as it is very subtle.
			super.render(matrixStack, mouseX, mouseY, partialTicks);
		} catch (IndexOutOfBoundsException e) {
			// Thanks mojang.
		}
		RenderHelper.endGlScissor();
	}

	@Override
	protected void renderBackground(PoseStack matrixStack) {}

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
