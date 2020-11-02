/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	 * Loads the library data from the file from {@link #getLibraryFile()}.
	 * @return A list of loaded library data.
	 */
	private List<LibraryEntryData> loadLibrary() {
		final List<LibraryEntryData> libraryEntries = new ArrayList<>();

		try (BufferedReader br = Files.newBufferedReader(getLibraryFile())) {
			final List<LibraryEntryData> loadedEntries = Tails.GSON.fromJson(br, new TypeToken<List<LibraryEntryData>>() {}.getType());
			if (loadedEntries != null && !loadedEntries.isEmpty())
				for (LibraryEntryData libEntry : loadedEntries)
					if (libEntry.partsData != null)
						libraryEntries.add(libEntry);
		} catch (IOException e) {
			Tails.LOGGER.catching(e);
		}

		return libraryEntries;
	}

	/**
	 * Writes the current library data to the file from {@link #getLibraryFile()}.<br>
	 * Remote entries are not written to the file.
	 */
	public void saveLibrary() {
		final List<LibraryEntryData> entries = new ArrayList<>();

		// Remove remote entries before saving.
		for (LibraryEntryData libraryListEntry : libraryEntries)
			if (!libraryListEntry.remoteEntry)
				entries.add(libraryListEntry);

		try (BufferedWriter bw = Files.newBufferedWriter(getLibraryFile())) {
			Tails.GSON.toJson(entries, bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the path to the library file.<br>
	 * By default, this is {@code tailslibrary.json} in the game directory.
	 * @return The library file.
	 */
	protected Path getLibraryFile() {
		final Path libraryFile = Paths.get(".", "tailslibrary.json");

		if (!Files.exists(libraryFile))
			try {
				Files.createFile(libraryFile);
			} catch (IOException e) {
				Tails.LOGGER.error("Failed to create library file!", e);
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
