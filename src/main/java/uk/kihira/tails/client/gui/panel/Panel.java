/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import org.apache.commons.lang3.Validate;

import net.minecraft.util.text.StringTextComponent;
import uk.kihira.tails.client.gui.LayeredScreen;
import uk.kihira.tails.client.gui.BaseScreen;

public abstract class Panel<T extends LayeredScreen> extends BaseScreen {

	protected final T parent;
	public int left;
	public int top;
	public int right;
	public int bottom;
	public boolean alwaysReceiveMouse = false;
	public boolean enabled = true;

	public Panel(T parent, int x, int y, int width, int height) {
		super(new StringTextComponent(""));
		Validate.isInstanceOf(LayeredScreen.class, parent);

		this.parent = parent;
		this.left = x;
		this.top = y;
		this.right = x + width;
		this.bottom = y + height;
	}

	public void resize(int x, int y, int newWidth, int newHeight) {
		left = x;
		top = y;
		right = x + newWidth;
		bottom = y + newHeight;
	}

	public void setHeight(int height) {
		this.height = height;
		bottom = top + height;
	}

	public void setWidth(int width) {
		this.width = width;
		right = left + width;
	}

	public T getParent() {
		return parent;
	}
}