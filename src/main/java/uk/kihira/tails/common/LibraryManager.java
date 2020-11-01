/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common.network.LibraryEntriesMessage;

public class LibraryManager {

	public final List<LibraryEntryData> libraryEntries;

	public LibraryManager() {
		libraryEntries = loadLibrary();
	}

	/**
	 * Adds the entries and saves
	 * @param entries The entries to add
	 */
	public void addEntries(List<? extends LibraryEntryData> entries) {
		libraryEntries.addAll(entries);
	}

	public void addEntry(LibraryEntryData data) {
		libraryEntries.add(data);
	}

	public void removeEntry(LibraryEntryData data) {
		libraryEntries.remove(data);
	}

	/**
	 * Removes remote entries from the list
	 */
	public void removeRemoteEntries() {
		libraryEntries.removeIf(entry -> entry.remoteEntry);
	}

	/**
	 * Loads the library of entries from disk
	 */
	private List<LibraryEntryData> loadLibrary() {
		final Gson gson = Tails.GSON;
		final ArrayList<LibraryEntryData> libraryEntries = new ArrayList<>();
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(getLibraryFile());
			final List<LibraryEntryData> loadedEntries = gson.fromJson(fileReader, new TypeToken<List<LibraryEntryData>>() {}.getType());
			if (loadedEntries != null && loadedEntries.size() > 0)
				for (LibraryEntryData libEntry : loadedEntries)
					if (libEntry.partsData != null)
						libraryEntries.add(libEntry);

		} catch (FileNotFoundException e) {
			Tails.LOGGER.catching(e);
		} finally {
			IOUtils.closeQuietly(fileReader);
		}
		return libraryEntries;
	}

	/**
	 * Saves the library to disk
	 */
	public void saveLibrary() {
		final List<LibraryEntryData> entries = new ArrayList<>();
		FileWriter fileWriter = null;

		// Remove remote entries before saving.
		for (LibraryEntryData libraryListEntry : libraryEntries)
			if (!libraryListEntry.remoteEntry)
				entries.add(libraryListEntry);

		try {
			fileWriter = new FileWriter(getLibraryFile());
			Tails.GSON.toJson(entries, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fileWriter);
		}
	}

	private File getLibraryFile() {
		final File libraryFile = new File("tailslibrary.json");

		if (!libraryFile.exists())
			try {
				if (!libraryFile.createNewFile())
					Tails.LOGGER.error("Failed to create a library file!");
			} catch (IOException e) {
				Tails.LOGGER.error("Failed to create a library file!", e);
			}
		return libraryFile;
	}

	public static class ClientLibraryManager extends LibraryManager {

		@Override
		public void addEntries(List<? extends LibraryEntryData> entries) {
			super.addEntries(entries);
			final Screen guiScreen = Minecraft.getInstance().currentScreen;

			if (guiScreen instanceof EditorScreen) {
				final EditorScreen editor = (EditorScreen) guiScreen;
				if (editor.getLibraryPanel() != null && editor.getLibraryInfoPanel() != null)
					((EditorScreen) guiScreen).getLibraryPanel().initList();
				((EditorScreen) guiScreen).getLibraryInfoPanel().setEntry(null);
			}
		}

		@Override
		public void removeEntry(final LibraryEntryData data) {
			if (data.remoteEntry)
				Tails.CHANNEL.sendToServer(new LibraryEntriesMessage(Collections.singletonList(data), true));
			else
				super.removeEntry(data);
		}
	}
}
