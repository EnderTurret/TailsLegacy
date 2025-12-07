/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.forge.client.gui.panel.Panel;

@Internal
public abstract class BaseScreen extends Screen {

	protected BaseScreen(Component title) {
		super(title);
	}

	@Override
	public void renderBackground(PoseStack poseStack) {}

	@Override
	public void removed() {
		for (Widget renderable : renderables)
			if (renderable instanceof Panel panel)
				panel.removed();

		super.removed();
	}

	// ===== Visibility ======

	public Font font() {
		return font;
	}

	@Override
	public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
		return super.addRenderableWidget(widget);
	}
}