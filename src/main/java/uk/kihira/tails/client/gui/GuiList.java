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
import com.mojang.blaze3d.systems.RenderSystem;

public class GuiList<T extends ExtendedList.AbstractListEntry<T>> extends ExtendedList<T> {

    private final IListCallback<T> parent;
    private int currentIndex;

    public GuiList(IListCallback<T> parent, int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
        super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
        this.parent = parent;
        this.replaceEntries(entries);
        //x0 = -3;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
    protected void renderBackground(MatrixStack matrixStack) {}

    @Override
    public int getRowWidth() {
    	return width;
    }

    /*@Override
    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
        this.currentIndex = index;
        if (this.parent != null) this.parent.onEntrySelected(this, index, this.getEntry(index));
    }*/

    @Override
    protected int getScrollbarPosition() {
        return this.x1/* - 6*/;
    }

    @Override
    public boolean isSelectedItem(int index) {
    	return super.isSelectedItem(index);
    }

    /*@Override
    public int getWidth() {
        return width - 8;
    }*/

    public int getItemHeight() {
    	return itemHeight;
    }
}
