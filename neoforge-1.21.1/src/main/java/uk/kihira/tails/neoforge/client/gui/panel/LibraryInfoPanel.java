/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.LocalPartManager;
import uk.kihira.tails.neoforge.client.RenderHelper;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.LibraryListEntry;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;
import uk.kihira.tails.neoforge.client.gui.widget.RelativeTextBox;
import uk.kihira.tails.neoforge.client.toast.ToastManager;

@Internal
public final class LibraryInfoPanel extends Panel<EditorScreen> {

	private LibraryListEntry entry;

	private EditBox textField;
	private IconButton.Toggle favButton;
	private IconButton deleteButton;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/YY");

	public LibraryInfoPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		textField = new RelativeTextBox(this, font, left + 6, top + 6, right - left - 12, 15, null);
		textField.setMaxLength(16);
		addWidget(textField);

		addRenderableWidget(favButton = new IconButton.Toggle(left + 5, bottom - 20, TailsIcons.STAR, b -> {
			entry.data.favourite = ((IconButton.Toggle) b).toggled;
		}, Component.translatable("tails.gui.library.button.favorite")));

		addRenderableWidget(deleteButton = new IconButton(left + 21, bottom - 20, TailsIcons.DELETE, b -> {
			((IconButton) b).setHover(false);
			parent.getLibraryPanel().removeEntry(entry);
			setEntry(null);
		}, Component.translatable("tails.gui.library.button.delete")));

		addRenderableWidget(new IconButton(left + 68, bottom - 20, TailsIcons.EXPORT, b -> {
			final StringBuilder sb = new StringBuilder();
			final LibraryEntryData libData = getEntry().data;
			sb.append(libData.entryName).append(":");
			sb.append(libData.creatorUUID).append(":");
			sb.append(LocalPartManager.GSON.toJson(libData.partsData));

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, Component.translatable("tails.gui.library.info.toast.export"));
			GLFW.glfwSetClipboardString(minecraft.getWindow().getWindow(), sb.toString());
		}, Component.translatable("tails.gui.library.button.share")));

		setEntry(null);
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		renderBackground(gui, mouseX, mouseY, partialTick);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		gui.fillGradient(left + 3, top + 3, right - 3, bottom - 3, 0, 0xFF000000, 0xFF000000);

		if (entry != null) {
			textField.render(gui, mouseX, mouseY, partialTick);

			int index = 0;

			final int xOffset = left;
			final int yOffset = top;
			for (ClientPartInfo partInfo : ((ClientPartsData) entry.data.partsData).getParts()) {
				String trans = partInfo.getPart() == null ? partInfo.getPartId().toString() : I18n.get(partInfo.getPart().getTranslationKey());
				RenderHelper.drawStringMultiLine(gui, font, trans,
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4),
						0xFFFFFF);

				trans = partInfo.getSubType() == null ? partInfo.getSubTypeId().toString() : I18n.get(partInfo.getPart().getTranslationKey() + ".subtype." + partInfo.getSubTypeId());
				RenderHelper.drawStringMultiLine(gui, font, trans,
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 1),
						0xFFFFFF);

				trans = partInfo.getPartTexture() == null ? partInfo.getTextureId().toString() : I18n.get(partInfo.getPart().getTranslationKey() + ".texture." + partInfo.getTextureId());
				RenderHelper.drawStringMultiLine(gui, font, trans,
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 2),
						0xFFFFFF);

				for (int i = 1; i < 4; i++)
					gui.fill(
							xOffset + (right - left) - 4 - 8 * i,
							yOffset + 32 + (index * 4 + 3) * 8,
							xOffset + (right - left) - 4 + 7 - 8 * i,
							yOffset + 32 + 7 + (index * 4 + 3) * 8,
							0xFF000000 | partInfo.getTints()[i - 1]);

				index++;
			}

			gui.drawString(font, I18n.get("tails.gui.library.info.created") + ":", left + 5, bottom - 59, 0xAAAAAA);
			gui.drawString(font, entry.data.creatorName, right - 5 - font.width(entry.data.creatorName), bottom - 50, 0xAAAAAA);
			gui.drawString(font, I18n.get("tails.gui.library.info.createdate") + ":", left + 5, bottom - 41, 0xAAAAAA);
			final String date = DATE_FORMAT.format(new Date(entry.data.creationDate));
			gui.drawString(font, date, right - 5 - font.width(date), bottom - 32, 0xAAAAAA);
		}

		super.render(gui, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		final boolean handled = super.keyPressed(keyCode, scanCode, modifiers);

		if (handled && entry != null) {
			entry.data.entryName = textField.getValue();
			parent.getLibraryPanel().libraryChanged = true;
		}

		return handled || textField.canConsumeInput();
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		final boolean handled = super.charTyped(codePoint, modifiers);

		if (handled && entry != null) {
			entry.data.entryName = textField.getValue();
			parent.getLibraryPanel().libraryChanged = true;
		}

		return handled;
	}

	public void setEntry(@Nullable LibraryListEntry entry) {
		this.entry = entry;

		final boolean visible = entry != null;

		if (visible) {
			favButton.toggled = entry.data.favourite;
			textField.setValue(entry.data.entryName);
		}

		textField.setVisible(visible);

		for (Renderable renderable : renderables)
			if (renderable instanceof AbstractWidget widget)
				widget.visible = visible;
	}

	@Nullable
	public LibraryListEntry getEntry() {
		return entry;
	}
}
