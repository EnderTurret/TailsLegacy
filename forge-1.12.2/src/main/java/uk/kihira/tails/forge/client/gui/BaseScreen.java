/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;

import uk.kihira.tails.forge.client.gui.panel.Panel;
import uk.kihira.tails.forge.client.gui.widget.ListWidget;

@Internal
public abstract class BaseScreen extends GuiScreen {

	private final List<Object> renderables = new ArrayList<>();

	protected BaseScreen() {
		super();
	}

	@Override
	public void drawDefaultBackground() {}

	@Override
	public void onGuiClosed() {
		for (Object renderable : renderables)
			if (renderable instanceof Panel)
				((Panel) renderable).removed();

		super.onGuiClosed();
	}

	public <T> T addRenderableWidget(T widget) {
		renderables.add(widget);
		if (widget instanceof GuiButton) buttonList.add((GuiButton) widget);
		return widget;
	}

	public static void setGuiComponentVisible(Object component, boolean visible) {
		if (component instanceof GuiButton)
			((GuiButton) component).visible = visible;
		else if (component instanceof GuiTextField)
			((GuiTextField) component).setVisible(visible);
		else if (component instanceof ListWidget)
			((ListWidget<?>) component).visible = visible;
		else
			throw new IllegalArgumentException("Unhandled component type " + component);
	}

	private int lastMouseX, lastMouseY;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		lastMouseX = mouseX;
		lastMouseY = mouseY;

		super.drawScreen(mouseX, mouseY, partialTick);

		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).render(mouseX, mouseY, partialTick);
			else if (component instanceof GuiTextField)
				((GuiTextField) component).drawTextBox();
			else if (component instanceof GuiSlot)
				((GuiSlot) component).drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).keyTyped(typedChar, keyCode);
			else if (component instanceof GuiTextField)
				((GuiTextField) component).textboxKeyTyped(typedChar, keyCode);

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).mouseClicked(mouseX, mouseY, mouseButton);
			else if (component instanceof ListWidget)
				((ListWidget) component).mouseClicked(mouseX, mouseY, mouseButton);
			else if (component instanceof GuiTextField)
				((GuiTextField) component).mouseClicked(mouseX, mouseY, mouseButton);

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).mouseReleased(mouseX, mouseY, state);
			else if (component instanceof ListWidget)
				((ListWidget) component).mouseReleased(mouseX, mouseY, state);

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		int scrollAmount = Mouse.getEventDWheel();
		if (scrollAmount > 0) scrollAmount = 1;
		else if (scrollAmount < 0) scrollAmount = -1;

		if (scrollAmount != 0)
			for (Object component : renderables)
				if (component instanceof Panel && ((Panel) component).visible)
					((Panel) component).mouseScrolled(lastMouseX, lastMouseY, scrollAmount);

		for (Object component : renderables)
			if (component instanceof ListWidget)
				((ListWidget) component).handleMouseInput();

		super.handleMouseInput();
	}

	// ===== Visibility ======

	public FontRenderer font() {
		return fontRenderer;
	}
}