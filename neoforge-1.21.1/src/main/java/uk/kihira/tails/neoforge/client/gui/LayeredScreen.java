/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.neoforge.client.gui.panel.Panel;

/**
 * A screen that has multiple layers, each with an arbitrary number of {@link Panel Panels}.
 */
@Internal
public abstract class LayeredScreen extends BaseScreen {

	// 0 is bottom layer.
	private final List<List<Panel<?>>> layers = new ArrayList<>();

	@Internal
	public LayeredScreen(int layerCount, Component title) {
		super(title);
		for (int i = 0; i < layerCount; i++)
			layers.add(new ArrayList<>());
	}

	@Internal
	public List<Panel<?>> getLayer(int layer) {
		return layers.get(layer);
	}

	public void setFocusedPanel(Panel<?> focus) {
		final Panel<?> old = (Panel<?>) getFocused();
		if (old == focus) return;

		if (old != null)
			for (GuiEventListener listener : old.children())
				if (listener instanceof AbstractWidget widget && widget.isFocused())
					widget.setFocused(false);

		setFocused(focus);
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
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled) {
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					panel.render(gui, mouseX, mouseY, partialTick);
				}

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

		super.render(gui, mouseX, mouseY, partialTick);

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled) {
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					panel.renderTooltips(gui, mouseX, mouseY, partialTick);
				}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseClicked(mouseX, mouseY, button))
					return true;

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseReleased(mouseX, mouseY, button))
					return true;

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseDragged(mouseX, mouseY, button, dragX, dragY))
					return true;

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY))
					panel.mouseMoved(mouseX, mouseY);

		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
					return true;

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != 258)
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

	@Override
	public void setFocused(GuiEventListener listener) {
		if (listener != null && !(listener instanceof Panel))
			throw new IllegalArgumentException("Can only focus panels!");
		super.setFocused(listener);
	}

	private static boolean shouldRecieveMouse(Panel<?> panel, double mouseX, double mouseY) {
		return panel.enabled && (mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse);
	}
}