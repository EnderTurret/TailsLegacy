/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.neoforge.client.RenderHelper;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;
import uk.kihira.tails.neoforge.common.TailsConfig;

@Internal
public final class PreviewPanel extends Panel {

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
		doRender = !parent.isLocalPlayer || !TailsConfig.CLIENT_INSTANCE.hidePreviewInThirdPerson.get() || minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
		if (!doRender) return;

		// Help
		addRenderableWidget(new IconButton(right - 18, 4, TailsIcons.QUESTION, b -> {}, Component.translatable("tails.gui.button.help.camera.0"), Component.translatable("tails.gui.button.help.camera.1")) {
			@Override
			protected boolean isValidClickButton(int button) {
				return false;
			}
		});

		// Reset Camera
		addRenderableWidget(new IconButton(right - 18, 22, TailsIcons.UNDO, b -> {
			yaw = 0;
			pitch = 8F;
			zoom = 1F;
		}, Component.translatable("tails.gui.button.reset.camera")));
	}

	@Override
	public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		gui.fillGradient(left, top, right, bottom, -900, 0xDD000000, 0xDD000000);
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		if (!doRender) return;

		renderBackground(gui, mouseX, mouseY, partialTick);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderHelper.startGlScissor(left, top, width, height);

		final int mcHeight = minecraft.getWindow().getGuiScaledHeight();
		final double factor = mcHeight / 4 * zoom;

		// Player
		RenderHelper.drawEntity(gui,
				left + width / 2,
				top + height / 2 + (int) factor,
				(int) factor,
				yaw, pitch,
				partialTick, parent.renderingEntity);

		RenderHelper.endGlScissor();

		super.render(gui, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		zoom += scrollY * .1;
		zoom = TailsMath.clamp(zoom, 1F, 3F);
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
				pitch = TailsMath.clamp(pitch, 4.8F, 13F);
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