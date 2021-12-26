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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import uk.kihira.tails.client.gui.panel.Panel;

/**
 * A screen that has multiple layers, each with an arbitrary number of {@link Panel Panels}.
 */
public abstract class LayeredScreen extends BaseScreen {

	//private static final int[] COLORS = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF00FFFF, 0xFFFF00FF};

	// 0 is bottom layer.
	private final List<List<Panel<?>>> layers = new ArrayList<>();

	public LayeredScreen(int layerCount, ITextComponent title) {
		super(title);
		for (int i = 0; i < layerCount; i++)
			layers.add(new ArrayList<>());
	}

	public List<Panel<?>> getLayer(int layer) {
		return layers.get(layer);
	}

	@Override
	protected void init() {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				panel.init(minecraft, panel.width, panel.height);
	}

	@Override
	public void resize(Minecraft mc, int width, int height) {
		super.resize(mc, width, height);
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				panel.resize(mc, panel.width, panel.height);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		//int color = 0;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled) {
					matrixStack.pushPose();
					matrixStack.translate(panel.left, panel.top, 0);
					RenderSystem.color4f(1f, 1f, 1f, 1f);

					panel.render(matrixStack, mouseX - panel.left, mouseY - panel.top, partialTicks);

					/*if (color == -1) {
						matrixStack.translate(0, 0, 100);

						final int c = COLORS[color >= COLORS.length ? COLORS.length - 1 : color];

						final int right = panel.right - panel.left;
						final int bottom = panel.bottom - panel.top;

						rect(matrixStack, 0, 0, right, bottom, c);

						font.drawStringWithShadow(matrixStack, panel.getClass().getSimpleName() + ": " + mouseX + ", " + mouseY, 3, 3, c);
					}

					color++;*/

					RenderSystem.disableLighting();
					matrixStack.popPose();
				}

		RenderSystem.color4f(1f, 1f, 1f, 1f);

		super.render(matrixStack, mouseX, mouseY, partialTicks);

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled) {
					matrixStack.pushPose();
					matrixStack.translate(panel.left, panel.top, 0);
					RenderSystem.color4f(1f, 1f, 1f, 1f);

					panel.renderTooltips(matrixStack, mouseX - panel.left, mouseY - panel.top, partialTicks);

					RenderSystem.disableLighting();
					matrixStack.popPose();
				}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseClicked(mouseX - panel.left, mouseY - panel.top, button))
					return true;

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseReleased(mouseX - panel.left, mouseY - panel.top, button))
					return true;

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseDragged(mouseX - panel.left, mouseY - panel.top, button, dragX, dragY))
					return true;

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY))
					panel.mouseMoved(mouseX - panel.left, mouseY - panel.top);

		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseScrolled(mouseX - panel.left, mouseY - panel.top, delta))
					return true;

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled && panel.keyPressed(keyCode, scanCode, modifiers))
					return true;

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled && panel.keyReleased(keyCode, scanCode, modifiers))
					return true;

		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled && panel.charTyped(codePoint, modifiers))
					return true;

		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public void removed() {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				panel.removed();

		super.removed();
	}

	private static boolean shouldRecieveMouse(Panel<?> panel, double mouseX, double mouseY) {
		return panel.enabled && (mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse);
	}
}
