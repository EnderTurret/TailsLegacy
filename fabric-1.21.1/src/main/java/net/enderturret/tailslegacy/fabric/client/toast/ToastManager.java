/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public final class ToastManager {

	public static final ToastManager INSTANCE = new ToastManager();

	private final List<Toast> toasts = new ArrayList<>();

	private ToastManager() {}

	public void createToast(int x, int y, Component text) {
		final Font fontRenderer = Minecraft.getInstance().font;
		final FormattedCharSequence processor = text.getVisualOrderText();
		final int stringWidth = fontRenderer.width(processor);
		toasts.add(new Toast(x, y, stringWidth + 10,  stringWidth * 3, processor));
	}

	public void createCenteredToast(int x, int y, int maxWidth, Component text) {
		final Font fontRenderer = Minecraft.getInstance().font;
		final int stringWidth = fontRenderer.width(text);
		if (stringWidth > maxWidth) {
			final List<FormattedCharSequence> strings = fontRenderer.split(text, maxWidth);
			toasts.add(new Toast(x - maxWidth / 2 - 5, y, maxWidth + 10, text.getString().length() * 3, strings.toArray(new FormattedCharSequence[strings.size()])));
		} else
			toasts.add(new Toast(x - stringWidth / 2 - 5, y, stringWidth + 10, text.getString().length() * 3, text.getVisualOrderText()));
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(ToastManager::onClientTickPost);
	}

	static void onClientTickPost(Minecraft mc) {
		final Iterator<Toast> toasts = INSTANCE.toasts.iterator();
		while (toasts.hasNext()) {
			final Toast toast = toasts.next();
			toast.time--;
			if (toast.time <= 0) toasts.remove();
		}
	}

	public static void extractRenderState(Screen screen, GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		gui.pose().pushMatrix();
		gui.pose().translate(0, 0);

		for (Toast toast : INSTANCE.toasts)
			toast.drawToast(gui, mouseX, mouseY);

		gui.pose().popMatrix();
	}
}