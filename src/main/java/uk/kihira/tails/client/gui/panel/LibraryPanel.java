/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.LibraryListEntry;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.gui.widget.ListWidget;
import uk.kihira.tails.client.gui.widget.RelativeTextBox;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;

public final class LibraryPanel extends Panel<EditorScreen> {

	private static final LibrarySorter SORTER = new LibrarySorter();
	private ListWidget<LibraryListEntry> list;
	private EditBox searchField;
	private boolean libraryChanged = false;

	public LibraryPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	public ListWidget<LibraryListEntry> getList() {
		return list;
	}

	@Override
	public void init() {
		initList();

		addRenderableWidget(new ExtendedButton(3, bottom - top - 18, right - left - 6, 15, Component.translatable("tails.gui.button.reload_library"), b -> {
			Tails.PROXY.getLibraryManager().reload(true);
			initList();
		}));
		addRenderableWidget(searchField = new RelativeTextBox(font, 5, bottom - top - 31, right - left - 10, 10, null));

		super.init();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		setBlitOffset(-100);
		fillGradient(poseStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);

		list.render(poseStack, mouseX, mouseY, partialTick);

		setBlitOffset(0);

		super.render(poseStack, mouseX, mouseY, partialTick);

		setBlitOffset(30);

		RenderSystem.setShaderTexture(0, IconButton.iconsTextures);

		poseStack.pushPose();

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		poseStack.translate(right - left - 16, bottom - top - 32, 0);
		poseStack.scale(0.75F, 0.75F, 0F);

		blit(poseStack, 0, 0, 160, 0, 16, 16);

		poseStack.popPose();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		final boolean value = super.keyPressed(keyCode, scanCode, modifiers);

		if (value) {
			final List<LibraryListEntry> newEntries = filterListEntries(searchField.getValue().toLowerCase(Locale.ROOT));
			newEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));
			list.children().clear();
			list.children().addAll(newEntries);
		}

		return value;
	}

	public void initList() {
		final List<LibraryListEntry> libraryEntries = new ArrayList<>();
		for (LibraryEntryData data : Tails.PROXY.getLibraryManager().libraryEntries)
			libraryEntries.add(new LibraryListEntry(this, data));

		// Add in new entry creation.
		libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));

		libraryEntries.sort(SORTER);

		removeWidget(list);
		addWidget(list = new ListWidget<>(right - left, bottom - top - 34, 0, bottom - top - 34, 50, libraryEntries));
	}

	public void addSelectedEntry(LibraryListEntry entry) {
		list.children().add(entry);
		list.setSelected(entry);
		parent.getLibraryInfoPanel().setEntry(entry);
		libraryChanged = true;
	}

	public void removeEntry(LibraryListEntry entry) {
		Tails.PROXY.getLibraryManager().removeEntry(entry.data);
		list.children().remove(entry);
		libraryChanged = true;
	}

	private List<LibraryListEntry> filterListEntries(String filter) {
		final ArrayList<LibraryListEntry> filteredEntries = new ArrayList<>();
		final List<LibraryListEntry> entries = new ArrayList<>();

		for (LibraryEntryData data : Tails.PROXY.getLibraryManager().libraryEntries)
			entries.add(new LibraryListEntry(this, data));

		for (LibraryListEntry entry : entries)
			if (entry instanceof LibraryListEntry.NewLibraryListEntry || entry.data.entryName.toLowerCase(Locale.ROOT).contains(filter))
				filteredEntries.add(entry);
		return filteredEntries;
	}

	@Override
	public void removed() {
		if (libraryChanged)
			Tails.PROXY.getLibraryManager().saveLibrary();
		super.removed();
	}

	private static class LibrarySorter implements Comparator<LibraryListEntry> {

		@Override
		public int compare(LibraryListEntry entry1, LibraryListEntry entry2) {
			if (entry1.equals(entry2))
				return 0;

			if (entry1 instanceof LibraryListEntry.NewLibraryListEntry)
				return -1;
			if (entry2 instanceof LibraryListEntry.NewLibraryListEntry)
				return 1;

			// Put favorites at the top.
			if (entry1.data.favourite && !entry2.data.favourite)
				return -1;
			if (!entry1.data.favourite && entry2.data.favourite)
				return 1;

			return 0;
		}
	}
}
