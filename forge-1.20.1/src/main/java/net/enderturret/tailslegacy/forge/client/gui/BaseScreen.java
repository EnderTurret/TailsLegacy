/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.enderturret.tailslegacy.forge.client.gui.panel.Panel;

@Internal
public abstract class BaseScreen extends Screen {

	protected BaseScreen(Component title) {
		super(title);
	}

	@Override
	public void renderBackground(GuiGraphics gui) {}

	@Override
	public void removed() {
		for (Renderable renderable : renderables)
			if (renderable instanceof Panel panel)
				panel.removed();

		super.removed();
	}

	// ===== Visibility ======

	public Font font() {
		return font;
	}

	@Override
	public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
		return super.addRenderableWidget(widget);
	}
}