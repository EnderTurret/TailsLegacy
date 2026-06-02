/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.forge.client.RenderHelper;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.forge.common.TailsConfig;

@Internal
public final class PreviewPanel extends Panel {

	public static final int HELP = 600;
	public static final int RESET_CAMERA = 601;

	private float yaw = 0F;
	private float pitch = 8F;
	private float zoom = 1F;
	private double prevMouseX = -1;
	private double prevMouseY = -1;
	private boolean doRender;

	public PreviewPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		doRender = !parent.isLocalPlayer || !TailsConfig.CLIENT_INSTANCE.hidePreviewInThirdPerson() || parent.mc.gameSettings.thirdPersonView == 0;
		if (!doRender) return;

		// Help
		addRenderableWidget(new IconButton(HELP, right - 18, 4, TailsIcons.QUESTION) {
			@Override
			public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
				return false;
			}
		}).setTooltip(TailsComponents.PREVIEW_HELP.getFormattedText());

		// Reset Camera
		addRenderableWidget(new IconButton(RESET_CAMERA, right - 18, 22, TailsIcons.UNDO))
		.setTooltip(TailsComponents.RESET_CAMERA.getFormattedText());
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case HELP:
				break;
			case RESET_CAMERA:
				yaw = 0;
				pitch = 8F;
				zoom = 1F;
				break;
		}
	}

	@Override
	public void renderBackground() {
		if (!doRender) return;
		final float oldZ = zLevel;
		zLevel = -900;
		drawGradientRect(left, top, right, bottom, 0xDD000000, 0xDD000000);
		zLevel = oldZ;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		if (!doRender) return;

		GL11.glColor4f(1F, 1F, 1F, 1F);
		RenderHelper.startGlScissor(left, top, right, bottom);

		final int mcHeight = new ScaledResolution(parent.mc, parent.mc.displayWidth, parent.mc.displayHeight).getScaledHeight();
		final double factor = mcHeight / 4 * zoom;

		// Player
		RenderHelper.drawEntity(
				left + (right - left) / 2,
				top + (bottom - top) / 2 + (int) factor,
				(int) factor,
				yaw, pitch,
				partialTick, parent.renderingEntity);

		RenderHelper.endGlScissor();
	}

	@Override
	public boolean mouseScrolled(int mouseX, int mouseY, int direction) {
		if (!(mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom)) return super.mouseScrolled(mouseX, mouseY, direction);

		zoom += direction * .1;
		zoom = TailsMath.clamp(zoom, 1F, 3F);

		return true;
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (clickedMouseButton != 0) return;

		boolean handled = false;

		// Yaw
		if (prevMouseX != -1) {
			yaw += (mouseX - prevMouseX) * 1.5F;
			handled = true;
		}

		// Pitch
		if (prevMouseY != -1) {
			pitch -= (mouseY - prevMouseY) * 0.05F;
			pitch = TailsMath.clamp(pitch, 4.8F, 13F);
			handled = true;
		}

		if (handled) {
			prevMouseX = mouseX;
			prevMouseY = mouseY;
		}
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (prevMouseX == -1 && mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom) {
			prevMouseX = mouseX;
			prevMouseY = mouseY;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		prevMouseX = -1;
		prevMouseY = -1;
		super.mouseReleased(mouseX, mouseY, state);
	}
}