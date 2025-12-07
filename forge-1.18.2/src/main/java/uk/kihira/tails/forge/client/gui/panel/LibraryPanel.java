/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.LibraryListEntry;
import uk.kihira.tails.forge.client.gui.TailsComponents;
import uk.kihira.tails.forge.client.gui.widget.IconButton;
import uk.kihira.tails.forge.client.gui.widget.ListWidget;

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

		addRenderableWidget(searchField = new EditBox(parent.font(), left + 5, bottom - 31, right - left - 10, 10, TextComponent.EMPTY));
		addRenderableWidget(new ExtendedButton(left + 3, bottom - 18, right - left - 6, 15, TailsComponents.RELOAD_LIBRARY, b -> {
			TailsClientPlatform.get().getLibraryManager().reload(true);
			libraryChanged = false;
			initList("");
		}));

		searchField.setResponder(this::initList);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		super.renderButton(poseStack, mouseX, mouseY, partialTick);

		RenderSystem.setShaderTexture(0, IconButton.ICONS_TEXTURE);
		blit(poseStack, right - 14, bottom - 30, 0, 240, 8, 8);
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