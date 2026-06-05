/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.neoforge.client.RenderHelper;
import net.enderturret.tailslegacy.neoforge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.neoforge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.neoforge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.neoforge.common.TailsConfig;

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
		doRender = !parent.isLocalPlayer || !TailsConfig.CLIENT_INSTANCE.hidePreviewInThirdPerson.get() || parent.getMinecraft().options.getCameraType() == CameraType.FIRST_PERSON;
		if (!doRender) return;

		// Help
		addRenderableWidget(new IconButton(right - 18, 4, TailsIcons.QUESTION, _ -> {}) {
			@Override
			protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
				return false;
			}
		}).setTooltip(Tooltip.create(TailsComponents.PREVIEW_HELP));

		// Reset Camera
		addRenderableWidget(new IconButton(right - 18, 22, TailsIcons.UNDO, _ -> {
			yaw = 0;
			pitch = 8F;
			zoom = 1F;
		})).setTooltip(Tooltip.create(TailsComponents.RESET_CAMERA));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		if (!doRender) return;
		gui.fill(left, top, right, bottom, 0xDD000000);
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractWidgetRenderState(gui, mouseX, mouseY, partialTick);

		if (!doRender) return;

		final int mcHeight = parent.getMinecraft().getWindow().getGuiScaledHeight();
		final double factor = mcHeight / 4 * zoom;

		// Player
		RenderHelper.drawEntity(gui,
				left,
				top,
				left + width,
				top + height,
				(int) factor,
				yaw, pitch,
				partialTick, parent.renderingEntity);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!(mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom)) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

		zoom += scrollY * .1;
		zoom = TailsMath.clamp(zoom, 1F, 3F);

		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
		if (event.button() != 0) return false;

		boolean handled = false;

		// Yaw
		if (prevMouseX != -1) {
			yaw += (event.x() - prevMouseX) * 1.5F;
			handled = true;
		}

		// Pitch
		if (prevMouseY != -1) {
			pitch -= (event.y() - prevMouseY) * 0.05F;
			pitch = TailsMath.clamp(pitch, 4.8F, 13F);
			handled = true;
		}

		if (handled) {
			prevMouseX = event.x();
			prevMouseY = event.y();
		}

		return handled;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		if (prevMouseX == -1 && event.x() >= left && event.y() >= top && event.x() < right && event.y() < bottom) {
			prevMouseX = event.x();
			prevMouseY = event.y();
		}

		return super.mouseClicked(event, isDoubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		prevMouseX = -1;
		prevMouseY = -1;
		return super.mouseReleased(event);
	}
}