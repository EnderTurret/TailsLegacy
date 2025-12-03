/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import uk.kihira.tails.neoforge.client.gui.widget.ITooltip;

@Internal
public abstract class BaseScreen extends Screen {

	protected BaseScreen(Component title) {
		super(title);
	}

	@Override
	public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {}

	public void renderTooltips(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		for (Renderable btn : renderables)
			if (btn instanceof ITooltip tooltip && btn instanceof GuiEventListener listener && listener.isMouseOver(mouseX, mouseY)) {
				final List<FormattedCharSequence> tooltips = Objects.requireNonNull(tooltip.getTooltip(mouseX, mouseY));

				if (!tooltips.isEmpty())
					gui.renderTooltip(font, tooltips, mouseX, mouseY);

				break;
			}
	}
}