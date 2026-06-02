package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.List;

public interface TooltipProvider {

	public boolean isHovered(int mouseX, int mouseY);
	public List<String> getTooltip();
}