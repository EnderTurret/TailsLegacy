/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.network.chat.Component;

import uk.kihira.tails.neoforge.client.gui.panel.Panel;

/**
 * A screen that has multiple layers, each with an arbitrary number of {@link Panel Panels}.
 */
@Internal
public abstract class LayeredScreen extends BaseScreen {

	// 0 is bottom layer.
	private final List<List<Panel>> layers = new ArrayList<>();

	@Internal
	public LayeredScreen(int layerCount, Component title) {
		super(title);
		for (int i = 0; i < layerCount; i++)
			layers.add(new ArrayList<>());
	}

	@Internal
	public List<Panel> getLayer(int layer) {
		return layers.get(layer);
	}

	@Override
	public void removed() {
		for (List<Panel> layer : layers)
			for (Panel panel : layer)
				panel.removed();

		super.removed();
	}
}