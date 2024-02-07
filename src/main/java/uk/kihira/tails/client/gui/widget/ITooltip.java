/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.FormattedCharSequence;

/**
 * A generic interface for things with tooltips.
 */
public interface ITooltip {

	/**
	 * Returns a tooltip to display.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param mouseIdleTime
	 * @return The tooltip.
	 */
	@NotNull
	public List<FormattedCharSequence> getTooltip(int mouseX, int mouseY, float mouseIdleTime);
}