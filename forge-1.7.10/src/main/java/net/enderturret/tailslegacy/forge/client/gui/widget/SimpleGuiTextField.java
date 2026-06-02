/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.widget;

import java.util.function.Consumer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class SimpleGuiTextField extends GuiTextField {

	private Consumer<String> responder;

	public SimpleGuiTextField(int id, FontRenderer font, int x, int y, int width, int height) {
		super(font, x, y, width, height);
	}

	public void setGuiResponder(Consumer<String> responder) {
		this.responder = responder;
	}

	@Override
	public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
		final boolean ret = super.textboxKeyTyped(p_146201_1_, p_146201_2_);
		if (ret) responder.accept(getText());
		return ret;
	}
}