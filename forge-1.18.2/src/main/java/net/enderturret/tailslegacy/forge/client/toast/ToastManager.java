/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.profiling.ProfilerFiller;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import net.enderturret.tailslegacy.common.TailsPlatform;

@EventBusSubscriber(modid = TailsPlatform.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
	static void onDrawScreenPost(ScreenEvent.DrawScreenEvent.Post event) {
		final ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
		profiler.push("toastNotification");

		event.getPoseStack().pushPose();
		event.getPoseStack().translate(0, 0, 300);

		for (Toast toast : INSTANCE.toasts)
			toast.drawToast(event.getPoseStack(), event.getMouseX(), event.getMouseY());

		event.getPoseStack().popPose();

		profiler.pop();
	}
}
