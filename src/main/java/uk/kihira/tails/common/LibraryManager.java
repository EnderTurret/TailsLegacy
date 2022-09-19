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
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LibraryManager {

	private static final Type ENTRY_DATA_LIST = new TypeToken<List<LibraryEntryData>>() {}.getType();
	private static final Path LIBRARY_PATH = Paths.get("tailslibrary.json");

	public final List<LibraryEntryData> libraryEntries = new ArrayList<>();

	public LibraryManager() {}

	protected Gson getGson() {
		return Tails.SERVER_GSON;
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

	public void reload(boolean maybeBackup) {
		final List<LibraryEntryData> entries = loadLibrary();

		if (maybeBackup)
			// Create a backup of the old data, in case you did something questionable.
			if (!entries.equals(libraryEntries))
				saveLibrary(LIBRARY_PATH.resolveSibling("tailslibrary.json.bak"));

		libraryEntries.clear();
		libraryEntries.addAll(entries);
	}

	/**
	 * Loads the library data from the file from {@link #createLibraryFile()}.
	 * @return A list of loaded library data.
	 */
	private List<LibraryEntryData> loadLibrary() {
		final List<LibraryEntryData> libraryEntries = new ArrayList<>();

		if (Files.exists(LIBRARY_PATH))
			try (BufferedReader br = Files.newBufferedReader(createLibraryFile())) {
				final List<LibraryEntryData> loadedEntries = getGson().fromJson(br, ENTRY_DATA_LIST);
				if (loadedEntries != null && !loadedEntries.isEmpty())
					for (LibraryEntryData libEntry : loadedEntries)
						if (libEntry.partsData != null)
							libraryEntries.add(libEntry);
			} catch (Exception e) {
				Tails.LOGGER.error("Failed to load library entries!", e);
			}

		return libraryEntries;
	}

	/**
	 * Writes the current library data to the file from {@link #createLibraryFile()}.<br>
	 * Remote entries are not written to the file.
	 */
	public void saveLibrary() {
		saveLibrary(createLibraryFile());
	}

	protected void saveLibrary(Path to) {
		try (BufferedWriter bw = Files.newBufferedWriter(to, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			getGson().toJson(libraryEntries, bw);
		} catch (IOException e) {
			Tails.LOGGER.error("Exception writing library:", e);
		}
	}

	/**
	 * Returns the path to the library file.<br>
	 * By default, this is {@code tailslibrary.json} in the game directory.
	 * @return The library file.
	 */
	protected Path createLibraryFile() {
		return createFile(LIBRARY_PATH);
	}

	protected static Path createFile(Path file) {
		if (!Files.exists(file))
			try {
				Files.createFile(file);
			} catch (IOException e) {
				Tails.LOGGER.error("Failed to create library file!", e);
			}

		return file;
	}
}
