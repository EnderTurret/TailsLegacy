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
					gui.pose().pushPose();
					gui.pose().translate(panel.left, panel.top, 0);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

					panel.render(gui, mouseX - panel.left, mouseY - panel.top, partialTick);

					gui.pose().popPose();
				}

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

		super.render(gui, mouseX, mouseY, partialTick);

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled) {
					gui.pose().pushPose();
					gui.pose().translate(panel.left, panel.top, 0);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

					panel.renderTooltips(gui, mouseX - panel.left, mouseY - panel.top, partialTick);

					gui.pose().popPose();
				}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		final Panel<?> focused = (Panel<?>) getFocused();
		if (focused != null && focused.mouseClicked(mouseX - focused.left, mouseY - focused.top, button))
			return true;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (focused != panel && shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseClicked(mouseX - panel.left, mouseY - panel.top, button))
					return true;

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		final Panel<?> focused = (Panel<?>) getFocused();
		if (focused != null && focused.mouseReleased(mouseX - focused.left, mouseY - focused.top, button))
			return true;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (focused != panel && shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseReleased(mouseX - panel.left, mouseY - panel.top, button))
					return true;

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		final Panel<?> focused = (Panel<?>) getFocused();
		if (focused != null && focused.mouseDragged(mouseX - focused.left, mouseY - focused.top, button, dragX, dragY))
			return true;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (focused != panel && shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseDragged(mouseX - panel.left, mouseY - panel.top, button, dragX, dragY))
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
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		final Panel<?> focused = (Panel<?>) getFocused();
		if (focused != null && focused.mouseScrolled(mouseX - focused.left, mouseY - focused.top, scrollX, scrollY))
			return true;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (focused != panel && shouldRecieveMouse(panel, mouseX, mouseY) && panel.mouseScrolled(mouseX - panel.left, mouseY - panel.top, scrollX, scrollY))
					return true;

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != 258) {
			final Panel<?> focused = (Panel<?>) getFocused();
			if (focused != null && focused.keyPressed(keyCode, scanCode, modifiers)) return true;

			for (List<Panel<?>> layer : layers)
				for (Panel<?> panel : layer)
					if (focused != panel && panel.enabled && panel.keyPressed(keyCode, scanCode, modifiers))
						return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		final Panel<?> focused = (Panel<?>) getFocused();
		if (focused != null && focused.keyReleased(keyCode, scanCode, modifiers)) return true;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (focused != panel && panel.enabled && panel.keyReleased(keyCode, scanCode, modifiers))
					return true;

		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		final Panel<?> focused = (Panel<?>) getFocused();
		if (focused != null && focused.charTyped(codePoint, modifiers)) return true;

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (focused != panel && panel.enabled && panel.charTyped(codePoint, modifiers))
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

	/* TODO: Port this.
	@Override
	public boolean changeFocus(boolean forward) {
		final GuiEventListener focused = getFocused();
		final boolean hasFocused = focused != null;

		if (hasFocused && focused.changeFocus(forward))
			return true;

		final List<Panel<?>> children = collapsePanels();
		final int focusedIndex = children.indexOf(focused);
		final int startIndex;

		if (hasFocused && focusedIndex >= 0)
			startIndex = focusedIndex + (forward ? 1 : 0);
		else if (forward)
			startIndex = 0;
		else
			startIndex = children.size();

		final ListIterator<Panel<?>> it = children.listIterator(startIndex);
		final BooleanSupplier hasNext = forward ? it::hasNext : it::hasPrevious;
		final Supplier<Panel<?>> next = forward ? it::next : it::previous;

		while (hasNext.getAsBoolean()) {
			final Panel<?> listener = next.get();
			if (listener.changeFocus(forward)) {
				setFocused(listener);
				return true;
			}
		}

		setFocused(null);

		return false;
	}
	*/

	@Override
	public void setFocused(GuiEventListener listener) {
		if (listener != null && !(listener instanceof Panel))
			throw new IllegalArgumentException("Can only focus panels!");
		super.setFocused(listener);
	}

	private List<Panel<?>> collapsePanels() {
		final List<Panel<?>> ret = new ArrayList<>();

		for (List<Panel<?>> layer : layers)
			for (Panel<?> panel : layer)
				if (panel.enabled)
					ret.add(panel);

		return ret;
	}

	private static boolean shouldRecieveMouse(Panel<?> panel, double mouseX, double mouseY) {
		return panel.enabled && (mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse);
	}
}