/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ToastManager {

	public static final ToastManager INSTANCE = new ToastManager();

	private final ArrayList<Toast> toasts = new ArrayList<>();

	private ToastManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

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
	public void onClientTickPost(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			final Iterator<Toast> toasts = this.toasts.iterator();
			while (toasts.hasNext()) {
				final Toast toast = toasts.next();
				toast.time--;
				if (toast.time <= 0) toasts.remove();
			}
		}
	}

	@SubscribeEvent
	public void onDrawScreenPost(ScreenEvent.DrawScreenEvent.Post event) {
		final ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
		profiler.push("toastNotification");
		for (Toast toast : toasts)
			toast.drawToast(event.getPoseStack(), event.getMouseX(), event.getMouseY());
		profiler.pop();
	}
}
