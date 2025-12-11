/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

/**
 * A version of {@link GuiListExtended} that improves upon some things.
 *
 * @param <T> The list type.
 */
public class ListWidget<T extends GuiListExtended.IGuiListEntry> extends SimpleGuiList<T> {

	public boolean visible = true;

	public ListWidget(int width, int height, int top, int slotHeight, List<T> entries) {
		super(Minecraft.getMinecraft(), width, height, 0, top, slotHeight);
		replaceEntries(entries);
		selectionListener = item -> { if (item != null) onItemSelected(item); };
	}

	public ListWidget(int width, int height, int top, int slotHeight) {
		this(width, height, top, slotHeight, new ArrayList<>());
	}

	public void onItemSelected(T item) {}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		if (visible)
			super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {}

	@Override
	public void handleMouseInput() {
		if (visible) super.handleMouseInput();
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		return visible && super.mouseClicked(mouseX, mouseY, mouseEvent);
	}

	@Override
	public boolean mouseReleased(int x, int y, int mouseEvent) {
		return visible && super.mouseReleased(x, y, mouseEvent);
	}

	@Override
	public int getListWidth() {
		return width;
	}

	@Override
	protected int getScrollBarX() {
		return right - 6;
	}

	public int getItemHeight() {
		return slotHeight;
	}
}
