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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import uk.kihira.tails.common.TailsPlatform;

@EventBusSubscriber(modid = TailsPlatform.MOD_ID, value = Side.CLIENT)
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
			final List<String> strings = fontRenderer.listFormattedStringToWidth(text, maxWidth);
			toasts.add(new Toast(x - maxWidth / 2 - 5, y, maxWidth + 10, text.length() * 3, strings.toArray(new String[strings.size()])));
		} else
			toasts.add(new Toast(x - stringWidth / 2 - 5, y, stringWidth + 10, text.length() * 3, text));
	}

	@SubscribeEvent
	static void onClientTickPost(ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		final Iterator<Toast> toasts = INSTANCE.toasts.iterator();
		while (toasts.hasNext()) {
			final Toast toast = toasts.next();
			toast.time--;
			if (toast.time <= 0) toasts.remove();
		}
	}

	@SubscribeEvent
	static void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 300);

		for (Toast toast : INSTANCE.toasts)
			toast.drawToast(event.getMouseX(), event.getMouseY());

		GlStateManager.popMatrix();
	}
}