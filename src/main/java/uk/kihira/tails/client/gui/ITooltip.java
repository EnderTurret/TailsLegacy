/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import javax.annotation.Nonnull;

import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * Implemented by GuiButton's that has a tooltip
 */
@Nonnull
public interface ITooltip {
	List<IReorderingProcessor> getTooltip(int mouseX, int mouseY, float mouseIdleTime);
}
