/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

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

	@SuppressWarnings("unchecked")
	public <T> T addRenderableWidget(T widget) {
		renderables.add(widget);
		if (widget instanceof GuiButton) buttonList.add(widget);
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
	public void initGui() {
		renderables.clear();
		super.initGui();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).keyTyped(typedChar, keyCode);
			else if (component instanceof GuiTextField)
				((GuiTextField) component).textboxKeyTyped(typedChar, keyCode);

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		for (Object component : renderables)
			if (component instanceof Panel && ((Panel) component).visible)
				((Panel) component).mouseClicked(mouseX, mouseY, mouseButton);
			else if (component instanceof ListWidget)
				((ListWidget) component).func_148179_a(mouseX, mouseY, mouseButton);
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
	protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
		if (state != -1)
			for (Object component : renderables)
				if (component instanceof Panel && ((Panel) component).visible)
					((Panel) component).mouseReleased(mouseX, mouseY, state);
				else if (component instanceof ListWidget)
					((ListWidget) component).func_148181_b(mouseX, mouseY, state);

		super.mouseMovedOrUp(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() {
		int scrollAmount = Mouse.getEventDWheel();
		if (scrollAmount > 0) scrollAmount = 1;
		else if (scrollAmount < 0) scrollAmount = -1;

		if (scrollAmount != 0)
			for (Object component : renderables)
				if (component instanceof Panel && ((Panel) component).visible)
					((Panel) component).mouseScrolled(lastMouseX, lastMouseY, scrollAmount);

		super.handleMouseInput();
	}

	// ===== Visibility ======

	public FontRenderer font() {
		return fontRendererObj;
	}

	@Override
	public void func_146283_a(List textLines, int x, int y) {
		super.func_146283_a(textLines, x, y);
	}
}