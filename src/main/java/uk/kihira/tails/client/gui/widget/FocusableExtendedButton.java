/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

/**
 * Fixes the {@link ExtendedButton}'s rendering code to properly take into account focusing.
 * See also <a href="https://github.com/MinecraftForge/MinecraftForge/pull/9144">this Forge PR</a>.
 * @author EnderTurret
 */
public class FocusableExtendedButton extends ExtendedButton {

	public FocusableExtendedButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress);
	}

	@Override
	protected int getYImage(boolean isHovered) {
		return super.getYImage(isHoveredOrFocused());
	}
}