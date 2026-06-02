/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiButton;

import net.minecraftforge.fml.client.config.GuiButtonExt;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.LibraryListEntry;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.forge.client.gui.widget.ListWidget;
import net.enderturret.tailslegacy.forge.client.gui.widget.SimpleGuiTextField;

@Internal
public final class LibraryPanel extends Panel {

	public static final int LIST = 400;
	public static final int SEARCH_FIELD = 401;
	public static final int RELOAD = 402;

	private ListWidget<LibraryListEntry> list;
	private SimpleGuiTextField searchField;
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

		addRenderableWidget(searchField = new SimpleGuiTextField(SEARCH_FIELD, parent.font(), left + 5, bottom - 31, right - left - 10, 10));
		addRenderableWidget(new GuiButtonExt(RELOAD, left + 3, bottom - 18, right - left - 6, 15, TailsComponents.RELOAD_LIBRARY.getFormattedText()));

		searchField.setGuiResponder(this::initList);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case RELOAD:
				TailsClientPlatform.get().getLibraryManager().reload(true);
				libraryChanged = false;
				initList("");
				break;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		parent.mc.getTextureManager().bindTexture(IconButton.ICONS_TEXTURE);
		drawTexturedModalRect(right - 14, bottom - 30, 0, 240, 8, 8);
	}

	public void initList(String filter) {
		filter = filter.toLowerCase(Locale.ENGLISH);

		final List<LibraryListEntry> libraryEntries = new ArrayList<>();
		for (LibraryEntryData data : TailsClientPlatform.get().getLibraryManager().libraryEntries)
			if (StringUtils.isBlank(filter) || data.entryName.toLowerCase(Locale.ENGLISH).contains(filter))
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