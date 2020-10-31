/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ToastManager {

	public static final ToastManager INSTANCE = new ToastManager();

	private final ArrayList<Toast> toasts = new ArrayList<>();

	private ToastManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void createToast(int x, int y, ITextComponent text) {
		final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		final IReorderingProcessor processor = text.func_241878_f();
		final int stringWidth = fontRenderer.func_243245_a(processor);
		toasts.add(new Toast(x, y, stringWidth + 10,  stringWidth * 3, processor));
	}

	public void createCenteredToast(int x, int y, int maxWidth, ITextComponent text) {
		final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		final int stringWidth = fontRenderer.getStringPropertyWidth(text);
		if (stringWidth > maxWidth) {
			final List<IReorderingProcessor> strings = fontRenderer.trimStringToWidth(text, maxWidth);
			toasts.add(new Toast(x - maxWidth / 2 - 5, y, maxWidth + 10, text.getString().length() * 3, strings.toArray(new IReorderingProcessor[strings.size()])));
		} else
			toasts.add(new Toast(x - stringWidth / 2 - 5, y, stringWidth + 10, text.getString().length() * 3, text.func_241878_f()));
	}

	/*@SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        for (Toast toast : toasts) {
            if (toast.mouseOver) {
                toast.onMouseEvent(event);
            }
        }
    }*/

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
	public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
		final IProfiler profiler = Minecraft.getInstance().getProfiler();
		profiler.startSection("toastNotification");
		for (Toast toast : toasts)
			toast.drawToast(event.getMatrixStack(), event.getMouseX(), event.getMouseY());
		profiler.endSection();
	}
}
