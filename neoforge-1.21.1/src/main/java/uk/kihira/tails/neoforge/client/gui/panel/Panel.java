/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.neoforge.client.gui.BaseScreen;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.LayeredScreen;

/**
 * A panel, for use in {@link LayeredScreen LayeredScreens}.
 */
@Internal
public abstract class Panel extends BaseScreen {

	protected final EditorScreen parent;
	public int left;
	public int top;
	public int right;
	public int bottom;
	public boolean alwaysReceiveMouse = false;
	public boolean enabled = true;

	public Panel(EditorScreen parent, int x, int y, int width, int height) {
		super(Component.empty());

		this.parent = parent;
		left = x;
		top = y;
		right = x + width;
		bottom = y + height;
		this.width = width;
		this.height = height;
	}

	public void resize(int x, int y, int newWidth, int newHeight) {
		left = x;
		top = y;
		right = x + newWidth;
		bottom = y + newHeight;
		width = newWidth;
		height = newHeight;
	}

	public void setHeight(int height) {
		this.height = height;
		bottom = top + height;
	}

	public void setWidth(int width) {
		this.width = width;
		right = left + width;
	}

	public EditorScreen getParent() {
		return parent;
	}

	@Override
	public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		gui.fill(left, top, right, bottom, -400, 0xCC000000);
	}
}