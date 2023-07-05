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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.RenderHelper;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.common.TailsConfig;

@Internal
public final class PreviewPanel extends Panel<EditorScreen> {

	private float yaw = 0F;
	private float pitch = 8F;
	private float zoom = 1F;
	private double prevMouseX = -1;
	private double prevMouseY = -1;
	private boolean doRender;

	public PreviewPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		doRender = !TailsConfig.CLIENT_INSTANCE.hidePreviewInThirdPerson.get() || minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
		if (!doRender) return;

		// Help
		addRenderableWidget(new IconButton(right - left - 18, 4, IconButton.Icons.QUESTION, b -> {}, Component.translatable("tails.gui.button.help.camera.0"), Component.translatable("tails.gui.button.help.camera.1")) {
			@Override
			protected boolean isValidClickButton(int button) {
				return false;
			}
		});

		// Reset Camera
		addRenderableWidget(new IconButton(right - left - 18, 22, IconButton.Icons.UNDO, b -> {
			yaw = 0;
			pitch = 8F;
			zoom = 1F;
		}, Component.translatable("tails.gui.button.reset.camera")));
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		if (!doRender) return;

		gui.pose().pushPose();
		gui.pose().translate(0, 0, -900);

		// Background
		gui.fillGradient(0, 0, right - left, bottom - top, 0xFF000000, 0xFF000000);

		gui.pose().popPose();

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderHelper.startGlScissor(left, top, width, height);

		final int mcHeight = minecraft.getWindow().getGuiScaledHeight();

		// Player
		RenderHelper.drawEntity(left + width / 2, top + height / 2 + (int) (mcHeight / 4 * zoom),
				(int) (mcHeight / 4 * zoom),
				yaw, pitch, partialTick, Minecraft.getInstance().player);

		RenderHelper.endGlScissor();

		super.render(gui, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		zoom += (delta * .1);
		zoom = Mth.clamp(zoom, 1F, 3F);
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0) {
			// Yaw
			if (prevMouseX != -1)
				yaw += (mouseX - prevMouseX) * 1.5F;
			// Pitch
			if (prevMouseY != -1) {
				pitch -= (mouseY - prevMouseY) * 0.05F;
				pitch = Mth.clamp(pitch, 4.8F, 13F);
			}

			prevMouseX = mouseX;
			prevMouseY = mouseY;
			return true;
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