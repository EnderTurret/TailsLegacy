/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client.gui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.neoforge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.neoforge.client.gui.LibraryListEntry;
import net.enderturret.tailslegacy.neoforge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.neoforge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.neoforge.client.gui.widget.ListWidget;

@Internal
public final class LibraryPanel extends Panel {

	private ListWidget<LibraryListEntry> list;
	private EditBox searchField;
	boolean libraryChanged = false;

	public LibraryPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	public ListWidget<LibraryListEntry> getList() {
		return list;
	}

	@Override
	public void init() {
		addRenderableWidget(list = new ListWidget<>(right - left, bottom - top - 34, 0, 50));
		initList("");

		addRenderableWidget(searchField = new EditBox(parent.font(), left + 4, bottom - 32, right - left - 8, 12, Component.empty()));
		addRenderableWidget(new ExtendedButton(left + 3, bottom - 18, right - left - 6, 15, TailsComponents.RELOAD_LIBRARY, _ -> {
			TailsClientPlatform.get().getLibraryManager().reload(true);
			libraryChanged = false;
			initList("");
		}));

		searchField.setResponder(this::initList);
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractWidgetRenderState(gui, mouseX, mouseY, partialTick);

		gui.blit(RenderPipelines.GUI_TEXTURED, IconButton.ICONS_TEXTURE, right - 14, bottom - 30, 0, 240, 8, 8, 256, 256);
	}

	public void initList(String filter) {
		filter = filter.toLowerCase(Locale.ENGLISH);

		final List<LibraryListEntry> libraryEntries = new ArrayList<>();
		for (LibraryEntryData data : TailsClientPlatform.get().getLibraryManager().libraryEntries)
			if (filter.isBlank() || data.entryName.toLowerCase(Locale.ENGLISH).contains(filter))
				libraryEntries.add(new LibraryListEntry(this, data));

		// Add in new entry creation.
		libraryEntries.add(0, LibraryListEntry.makeNewEntryEntry(this));
		Collections.sort(libraryEntries);

		list.replaceEntries(libraryEntries);
	}

	public void addSelectedEntry(LibraryListEntry entry) {
		list.addEntry(entry);
		list.setSelected(entry);
		parent.getLibraryInfoPanel().setEntry(entry);
		libraryChanged = true;
	}

	public void removeEntry(LibraryListEntry entry) {
		TailsClientPlatform.get().getLibraryManager().removeEntry(entry.data);
		list.removeEntry(entry);
		libraryChanged = true;
	}

	public void save() {
		if (libraryChanged) {
			TailsClientPlatform.get().getLibraryManager().saveLibrary();
			libraryChanged = false;
		}
	}

	@Override
	public void removed() {
		save();
	}
}