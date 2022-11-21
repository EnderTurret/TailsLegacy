/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.RenderHelper;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.IconButton;

@Internal
public final class PreviewPanel extends Panel<EditorScreen> {

	private float yaw = 0F;
	private float pitch = 10F;
	private double prevMouseX = -1;
	private double prevMouseY = -1;
	private boolean doRender;

	public PreviewPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		doRender = Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
		if (!doRender) return;
		// Reset Camera
		addRenderableWidget(new IconButton(right - left - 18, 22, IconButton.Icons.UNDO, b -> {
			yaw = 0;
			pitch = 10F;
		}, Component.translatable("tails.gui.button.reset.camera")));
		// Help
		addRenderableWidget(new IconButton(right - left - 18, 4, IconButton.Icons.QUESTION, b -> {}, Component.translatable("tails.gui.button.help.camera.0"), Component.translatable("tails.gui.button.help.camera.1")));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (!doRender) return;
		setBlitOffset(-900);
		// Background
		fillGradient(poseStack, 0, 0, right - left, bottom - top, 0xFF000000, 0xFF000000);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		setBlitOffset(0);

		RenderHelper.startGlScissor(left, top, width, height);

		// Player
		RenderHelper.drawEntity(left + width / 2, top + height / 2 + Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4,
				Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4,
				yaw, pitch, partialTick, Minecraft.getInstance().player);

		RenderHelper.endGlScissor();

		super.render(poseStack, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0) {
			// Yaw
			if (prevMouseX != -1)
				yaw += (mouseX - prevMouseX) * 1.5F;
			// Pitch
			if (prevMouseY != -1) {
				pitch += (mouseY - prevMouseY) * 0.1F;
				pitch = Mth.clamp(pitch, 6, 10);
			}

			prevMouseX = mouseX;
			prevMouseY = mouseY;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		prevMouseX = -1;
		prevMouseY = -1;
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}
}