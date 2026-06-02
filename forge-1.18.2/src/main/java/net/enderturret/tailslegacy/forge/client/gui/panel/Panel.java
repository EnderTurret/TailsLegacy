/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.widget.ListWidget;

/**
 * A panel, for use in the {@link EditorScreen}.
 */
@Internal
public abstract class Panel extends AbstractWidget {

	protected final EditorScreen parent;
	protected final List<Widget> renderables = new ArrayList<>();

	public int left, right;
	public int top, bottom;

	public Panel(EditorScreen parent, int x, int y, int width, int height) {
		super(x, y, width, height, TextComponent.EMPTY);

		this.parent = parent;
		left = x;
		top = y;
		right = x + width;
		bottom = y + height;
	}

	public abstract void init();

	protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
		renderables.add(widget);
		return parent.addRenderableWidget(widget);
	}

	@Override
	protected boolean isValidClickButton(int button) { return false; }

	public void removed() {}

	public void resize(int x, int y, int newWidth, int newHeight) {
		renderables.clear();
		left = x;
		top = y;
		right = x + newWidth;
		bottom = y + newHeight;
		width = newWidth;
		height = newHeight;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
		right = left + width;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
		bottom = top + height;
	}

	public void setVisible(boolean value) {
		visible = value;
		setChildrenVisible(value);
	}

	protected void setChildrenVisible(boolean value) {
		for (Widget renderable : renderables)
			if (renderable instanceof AbstractWidget widget)
				widget.visible = value;
			else if (renderable instanceof ListWidget<?> widget)
				widget.visible = value;
	}

	public EditorScreen getParent() {
		return parent;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {}

	public void renderBackground(PoseStack poseStack) {
		fillGradient(poseStack, left, top, right, bottom, 0xCC000000, 0xCC000000, -400);
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}