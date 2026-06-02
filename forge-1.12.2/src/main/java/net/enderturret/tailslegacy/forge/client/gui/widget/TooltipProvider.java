/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.List;

public interface TooltipProvider {

	public boolean isHovered(int mouseX, int mouseY);
	public List<String> getTooltip();
}