/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

import net.enderturret.tailslegacy.forge.client.RenderHelper;

public class SimpleGuiList<E extends GuiListExtended.IGuiListEntry> extends GuiListExtended {

	protected final List<E> entries = new ArrayList<>();
	protected Consumer<E> selectionListener;
	protected int selection = -1;

	public SimpleGuiList(Minecraft mc, int width, int height, int left, int top, int slotHeight) {
		super(mc, width, height, top, top + height, slotHeight);
		this.left = left;
		right = left + width;
		setHasListHeader(false, 0);
		field_148163_i /* centerListVertically */ = false;
	}

	public List<E> getEntries() {
		return entries;
	}

	public void replaceEntries(Collection<E> entries) {
		this.entries.clear();
		this.entries.addAll(entries);
		setSelected(null);
	}

	public void addEntry(E entry) {
		entries.add(entry);
	}

	public void removeEntry(E entry) {
		final int idx = entries.indexOf(entry);
		if (idx == selection) setSelected(null);
		else if (idx < selection) selection--;
		entries.remove(idx);
	}

	@Override
	public E getListEntry(int index) {
		return entries.get(index);
	}

	@Override
	protected int getSize() {
		return entries.size();
	}

	@Override
	public boolean isSelected(int slotIndex) {
		return selection == slotIndex;
	}

	public E getSelected() {
		return selection == -1 ? null : entries.get(selection);
	}

	public void setSelected(E selected) {
		selection = selected == null ? -1 : entries.indexOf(selected);
		if (selectionListener != null)
			selectionListener.accept(selected);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		RenderHelper.startGlScissor(left, top, right, bottom);

		try {
			super.drawScreen(mouseX, mouseY, partialTicks);
		} finally {
			RenderHelper.endGlScissor();
		}
	}
}