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

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import net.enderturret.tailslegacy.forge.client.gui.BaseScreen;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;

/**
 * A panel, for use in the {@link EditorScreen}.
 */
@Internal
public abstract class Panel extends Gui {

	protected final EditorScreen parent;
	protected final List<Object> renderables = new ArrayList<>();

	public int left, right;
	public int top, bottom;
	public boolean visible = true;

	public Panel(EditorScreen parent, int x, int y, int width, int height) {
		this.parent = parent;
		left = x;
		top = y;
		right = x + width;
		bottom = y + height;
	}

	public abstract void init();

	protected <T> T addRenderableWidget(T widget) {
		renderables.add(widget);
		return parent.addRenderableWidget(widget);
	}

	public void removed() {}

	public void resize(int x, int y, int newWidth, int newHeight) {
		renderables.clear();
		left = x;
		top = y;
		right = x + newWidth;
		bottom = y + newHeight;
	}

	public void setVisible(boolean value) {
		visible = value;
		setChildrenVisible(value);
	}

	protected void setChildrenVisible(boolean value) {
		for (Object renderable : renderables)
			BaseScreen.setGuiComponentVisible(renderable, value);
	}

	public EditorScreen getParent() {
		return parent;
	}

	public void render(int mouseX, int mouseY, float partialTick) {}

	public void renderBackground() {
		final float oldZ = zLevel;
		zLevel = -400;
		drawGradientRect(left, top, right, bottom, 0xCC000000, 0xCC000000);
		zLevel = oldZ;
	}

	public void actionPerformed(GuiButton button) {}
	public boolean keyTyped(char typedChar, int keyCode) { return false; }
	public boolean mouseScrolled(int mouseX, int mouseY, int direction) { return false; }
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) { return false; }
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}
	public void mouseReleased(int mouseX, int mouseY, int state) {}
}