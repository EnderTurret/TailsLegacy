/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import uk.kihira.tails.client.gui.widget.ITooltip;

@Internal
public abstract class BaseScreen extends Screen {

	private int prevMouseX;
	private int prevMouseY;
	private float mouseIdleTicks;

	protected BaseScreen(Component title) {
		super(title);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
	}

	public void renderTooltips(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		for (Renderable btn : renderables)
			if (btn instanceof ITooltip tooltip && btn instanceof GuiEventListener listener && listener.isMouseOver(mouseX, mouseY)) {
				if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTick;
				else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;

				final List<FormattedCharSequence> tooltips = Objects.requireNonNull(tooltip.getTooltip(mouseX, mouseY, mouseIdleTicks));

				if (!tooltips.isEmpty())
					gui.renderTooltip(font, tooltips, mouseX, mouseY);

				prevMouseX = mouseX;
				prevMouseY = mouseY;
				break;
			}
	}

	public void rect(GuiGraphics gui, int x1, int y1, int x2, int y2, int color) {
		gui.hLine(x1, x2, y1, color);
		gui.hLine(x1, x2, y2, color);
		gui.vLine(x1, y1, y2, color);
		gui.vLine(x2, y1, y2, color);
	}
}
