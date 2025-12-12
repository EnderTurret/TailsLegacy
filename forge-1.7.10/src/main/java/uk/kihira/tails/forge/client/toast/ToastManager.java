/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import net.minecraftforge.client.event.GuiScreenEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import uk.kihira.tails.common.TailsPlatform;

public final class ToastManager {

	public static final ToastManager INSTANCE = new ToastManager();

	private final List<Toast> toasts = new ArrayList<>();

	private ToastManager() {}

	public void createToast(int x, int y, String text) {
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		final int stringWidth = fontRenderer.getStringWidth(text);
		toasts.add(new Toast(x, y, stringWidth + 10,  stringWidth * 3, text));
	}

	public void createCenteredToast(int x, int y, int maxWidth, String text) {
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		final int stringWidth = fontRenderer.getStringWidth(text);
		if (stringWidth > maxWidth) {
			@SuppressWarnings("unchecked")
			final List<String> strings = fontRenderer.listFormattedStringToWidth(text, maxWidth);
			toasts.add(new Toast(x - maxWidth / 2 - 5, y, maxWidth + 10, text.length() * 3, strings.toArray(new String[strings.size()])));
		} else
			toasts.add(new Toast(x - stringWidth / 2 - 5, y, stringWidth + 10, text.length() * 3, text));
	}

	@SubscribeEvent
	public void onClientTickPost(ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		final Iterator<Toast> toasts = INSTANCE.toasts.iterator();
		while (toasts.hasNext()) {
			final Toast toast = toasts.next();
			toast.time--;
			if (toast.time <= 0) toasts.remove();
		}
	}

	@SubscribeEvent
	public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 300);

		for (Toast toast : INSTANCE.toasts)
			toast.drawToast(event.mouseX, event.mouseY);

		GL11.glPopMatrix();
	}
}