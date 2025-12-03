/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.LibraryListEntry;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;
import uk.kihira.tails.neoforge.client.gui.widget.ListWidget;
import uk.kihira.tails.neoforge.client.gui.widget.RelativeTextBox;

@Internal
public final class LibraryPanel extends Panel {

	private ListWidget<LibraryListEntry> list;
	private EditBox searchField;
	boolean libraryChanged = false;

	public LibraryPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	public ListWidget<LibraryListEntry> getList() {
		return list;
	}

	@Override
	public void init() {
		addRenderableWidget(list = new ListWidget<>(right - left, bottom - top - 34, 0, 50, new ArrayList<>()));
		initList("");

		addRenderableWidget(searchField = new RelativeTextBox(this, parent.font(), left + 4, bottom - 32, right - left - 8, 12, Component.empty()));
		addRenderableWidget(new ExtendedButton(left + 3, bottom - 18, right - left - 6, 15, Component.translatable("tails.gui.button.reload_library"), b -> {
			TailsClientPlatform.get().getLibraryManager().reload(true);
			libraryChanged = false;
			initList("");
		}));

		searchField.setResponder(this::initList);
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(gui, mouseX, mouseY, partialTick);

		gui.blit(IconButton.ICONS_TEXTURE, right - 14, bottom - 30, 0, 240, 8, 8);
	}

	public void initList(String filter) {
		filter = filter.toLowerCase(Locale.ENGLISH);

		final List<LibraryListEntry> libraryEntries = new ArrayList<>();
		for (LibraryEntryData data : TailsClientPlatform.get().getLibraryManager().libraryEntries)
			if (filter.isBlank() || data.entryName.toLowerCase(Locale.ENGLISH).contains(filter))
				libraryEntries.add(new LibraryListEntry(this, data));

		// Add in new entry creation.
		libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this));
		libraryEntries.sort(LibraryListEntry.LibrarySorter.INSTANCE);

		list.replaceEntries(libraryEntries);
	}

	public void addSelectedEntry(LibraryListEntry entry) {
		list.children().add(entry);
		list.setSelected(entry);
		parent.getLibraryInfoPanel().setEntry(entry);
		libraryChanged = true;
	}

	public void removeEntry(LibraryListEntry entry) {
		TailsClientPlatform.get().getLibraryManager().removeEntry(entry.data);
		list.children().remove(entry);
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