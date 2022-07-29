/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.LibraryListEntry;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.gui.widget.RelativeTextField;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;

public class LibraryInfoPanel extends Panel<EditorScreen> {

	private LibraryListEntry entry;

	private EditBox textField;
	private IconButton.Toggle favButton;
	private IconButton deleteButton;

	public LibraryInfoPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		textField = new RelativeTextField(font, 6, 6, right - left - 12, 15, null);
		textField.setMaxLength(16);
		addWidget(textField);

		addRenderableWidget(favButton = new IconButton.Toggle(5, bottom - top - 20, IconButton.Icons.STAR, b -> {
			entry.data.favourite = ((IconButton.Toggle) b).toggled;
		}, Component.translatable("tails.gui.library.button.favorite")));
		addRenderableWidget(deleteButton = new IconButton(21, bottom - top - 20, IconButton.Icons.DELETE, b -> {
			((IconButton) b).setHover(false);
			parent.getLibraryPanel().removeEntry(entry);
			setEntry(null);
		}, Component.translatable("tails.gui.library.button.delete")));
		addRenderableWidget(new IconButton(68, bottom - top - 20, IconButton.Icons.EXPORT, b -> {
			final StringBuilder sb = new StringBuilder();
			final LibraryEntryData libData = getEntry().data;
			sb.append(libData.entryName).append(":");
			sb.append(libData.creatorUUID).append(":");
			sb.append(Tails.GSON.toJson(libData.partsData));

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, Component.translatable("tails.gui.library.info.toast.export"));
			GLFW.glfwSetClipboardString(minecraft.getWindow().getWindow(), sb.toString());
		}, Component.translatable("tails.gui.library.button.share")));

		super.init();

		setEntry(null);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		setBlitOffset(0);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);

		RenderSystem.setShaderColor(0F, 0F, 0F, 0F);

		setBlitOffset(10);
		fillGradient(matrixStack, 3, 3, right - left - 3, bottom - top - 3, 0xFF000000, 0xFF000000);

		if (entry != null) {
			textField.render(matrixStack, mouseX, mouseY, partialTicks);

			font.draw(matrixStack, I18n.get("tails.gui.library.info.created") + ":", 5, bottom - top - 59, 0xAAAAAA);
			font.draw(matrixStack, entry.data.creatorName, right - left - 5 - font.width(entry.data.creatorName), bottom - top - 50, 0xAAAAAA);
			font.draw(matrixStack, I18n.get("tails.gui.library.info.createdate") + ":", 5, bottom - top - 41, 0xAAAAAA);
			final String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
			font.draw(matrixStack, date, right - left - 5 - font.width(date), bottom - top - 32, 0xAAAAAA);
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		final boolean handled = super.keyPressed(keyCode, scanCode, modifiers);

		if (handled && entry != null) {
			entry.data.entryName = textField.getValue();
			Tails.PROXY.getLibraryManager().saveLibrary();
		}

		return handled || textField.canConsumeInput();
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		final boolean handled = super.charTyped(codePoint, modifiers);

		if (handled && entry != null) {
			entry.data.entryName = textField.getValue();
			Tails.PROXY.getLibraryManager().saveLibrary();
		}

		return handled;
	}

	public void setEntry(LibraryListEntry entry) {
		this.entry = entry;
		if (entry == null) {
			textField.setVisible(false);
			for (Widget button : renderables)
				if (button instanceof AbstractWidget)
					((AbstractWidget) button).visible = false;
		}
		else {
			favButton.toggled = entry.data.favourite;
			textField.setVisible(true);
			textField.setValue(entry.data.entryName);
			for (Widget button : renderables)
				if (button instanceof AbstractWidget)
					((AbstractWidget) button).visible = true;
		}
	}

	public LibraryListEntry getEntry() {
		return entry;
	}
}
