/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.neoforge.client.gui.panel.Panel;

/**
 * An {@link EditBox} that takes into account the {@link PoseStack} transformations when drawing the selection box.
 * Without this, the selection overlay will attempt to yeet itself as far off-screen as possible.
 * @author EnderTurret
 */
public final class RelativeTextBox extends EditBox {

	private final Panel parent;

	public RelativeTextBox(Panel parent, Font font, int x, int y, int width, int height, Component message) {
		super(font, x, y, width, height, message);
		this.parent = parent;
	}
}