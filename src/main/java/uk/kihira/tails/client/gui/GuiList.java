/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import uk.kihira.tails.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class GuiList<T extends ExtendedList.AbstractListEntry<T>> extends ExtendedList {

    private final IListCallback<T> parent;
    private final List<T> entries;
    private int currentIndex;

    public GuiList(IListCallback<T> parent, int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
        super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
        this.parent = parent;
        this.entries = entries;
        x0 = -3;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderHelper.startGlScissor(this.x0, this.y0, this.width + 3, this.height);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderHelper.endGlScissor();
    }

    @Override
    protected void renderBackground(MatrixStack matrixStack) {}

    /*@Override
    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
        this.currentIndex = index;
        if (this.parent != null) this.parent.onEntrySelected(this, index, this.getEntry(index));
    }*/

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    @Override
    protected boolean isSelectedItem(int index) {
        return this.currentIndex == index;
    }

    @Override
    public T getEntry(int index) {
        return this.entries.isEmpty() ? null : this.entries.get(index);
    }

    @Override
    protected int getItemCount() {
        return this.entries.size();
    }

    @Override
    public int getWidth() {
        return width - 8;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public List<T> getEntries() {
        return this.entries;
    }

    public int getItemHeight() {
    	return itemHeight;
    }
}
