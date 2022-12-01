/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.widget.ITooltip;

@Internal
public abstract class BaseScreen extends Screen {

	private int prevMouseX;
	private int prevMouseY;
	private float mouseIdleTicks;

	protected BaseScreen(Component title) {
		super(title);
	}

	public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		for (Widget btn : renderables)
			if (btn instanceof ITooltip tooltip && btn instanceof GuiEventListener listener && listener.isMouseOver(mouseX, mouseY)) {
				if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTick;
				else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;

				renderTooltip(poseStack, tooltip.getTooltip(mouseX, mouseY, mouseIdleTicks), mouseX, mouseY, font);

				prevMouseX = mouseX;
				prevMouseY = mouseY;
				break;
			}
	}

	public void rect(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {
		hLine(poseStack, x1, x2, y1, color);
		hLine(poseStack, x1, x2, y2, color);
		vLine(poseStack, x1, y1, y2, color);
		vLine(poseStack, x2, y1, y2, color);
	}
}
