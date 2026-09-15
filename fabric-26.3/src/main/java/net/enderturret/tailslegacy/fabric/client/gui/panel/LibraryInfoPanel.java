/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.gui.panel;

import java.util.Date;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.sdl.SDLClipboard;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.common.client.gui.panel.BaseLibraryInfoPanel;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.fabric.client.gui.EditorScreen;
import net.enderturret.tailslegacy.fabric.client.gui.LibraryListEntry;
import net.enderturret.tailslegacy.fabric.client.gui.TailsComponents;
import net.enderturret.tailslegacy.fabric.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.fabric.client.toast.ToastManager;

@Internal
public final class LibraryInfoPanel extends Panel implements BaseLibraryInfoPanel {

	private LibraryListEntry entry;

	private EditBox textField;
	private IconButton.Toggle favButton;
	private IconButton deleteButton;

	public LibraryInfoPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		textField = new EditBox(parent.font(), left + 6, top + 6, right - left - 12, 15, Component.empty());
		textField.setMaxLength(19);
		addRenderableWidget(textField);
		textField.setResponder(str -> {
			if (entry != null) {
				entry.data.entryName = str;
				parent.getLibraryPanel().libraryChanged = true;
			}
		});

		addRenderableWidget(favButton = new IconButton.Toggle(left + 5, bottom - 20, TailsIcons.STAR, _ -> {
			entry.data.favourite = favButton.toggled;
		})).setTooltip(Tooltip.create(TailsComponents.FAVORITE_BUTTON));

		addRenderableWidget(deleteButton = new IconButton(left + 21, bottom - 20, TailsIcons.DELETE, _ -> {
			deleteButton.setHover(false);
			parent.getLibraryPanel().removeEntry(entry);
			setEntry(null);
		})).setTooltip(Tooltip.create(TailsComponents.DELETE_BUTTON));

		addRenderableWidget(new IconButton(left + 68, bottom - 20, TailsIcons.EXPORT, _ -> {
			final String export = exportString(getEntry().data);

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, TailsComponents.EXPORTED_MESSAGE);
			SDLClipboard.SDL_SetClipboardText(export);
		})).setTooltip(Tooltip.create(TailsComponents.SHARE_BUTTON));

		setEntry(null);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(gui, mouseX, mouseY, partialTick);
		gui.fill(left + 3, top + 3, right - 3, bottom - 3, 0xFF000000);
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractWidgetRenderState(gui, mouseX, mouseY, partialTick);

		if (entry != null) {
			int index = 0;

			final int xOffset = left;
			final int yOffset = top;
			for (ClientPartInfo partInfo : ((ClientPartsData) entry.data.partsData).getParts()) {
				gui.text(parent.font(), TailsComponents.getPartName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4),
						0xFFFFFFFF);

				gui.text(parent.font(), TailsComponents.getSubTypeName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 1),
						0xFFFFFFFF);

				gui.text(parent.font(), TailsComponents.getTextureName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 2),
						0xFFFFFFFF);

				for (int i = 1; i < 4; i++)
					gui.fill(
							xOffset + (right - left) - 4 - 8 * i,
							yOffset + 32 + (index * 4 + 3) * 8,
							xOffset + (right - left) - 4 + 7 - 8 * i,
							yOffset + 32 + 7 + (index * 4 + 3) * 8,
							0xFF000000 | partInfo.getTints()[i - 1]);

				index++;
			}

			gui.text(parent.font(), TailsComponents.LIBRARY_ENTRY_CREATOR, left + 5, bottom - 59, 0xFFAAAAAA);
			gui.text(parent.font(), entry.data.creatorName, right - 5 - parent.font().width(entry.data.creatorName), bottom - 50, 0xFFAAAAAA);
			gui.text(parent.font(), TailsComponents.LIBRARY_ENTRY_CREATION_DATE, left + 5, bottom - 41, 0xFFAAAAAA);
			final String date = DATE_FORMAT.format(new Date(entry.data.creationDate));
			gui.text(parent.font(), date, right - 5 - parent.font().width(date), bottom - 32, 0xFFAAAAAA);
		}
	}

	public void setEntry(@Nullable LibraryListEntry entry) {
		this.entry = entry;

		final boolean visible = entry != null;

		if (visible) {
			favButton.toggled = entry.data.favourite;
			textField.setValue(entry.data.entryName);
		}

		for (AbstractWidget widget : renderables)
			widget.visible = visible;
	}

	@Nullable
	public LibraryListEntry getEntry() {
		return entry;
	}
}