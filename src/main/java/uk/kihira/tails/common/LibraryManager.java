/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
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

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * As you may have guessed, this manages the Tails library.
 * It handles saving/loading the library as well as manipulating its entries.
 */
@Internal
public class LibraryManager {

	private static final Type ENTRY_DATA_LIST = new TypeToken<List<LibraryEntryData>>() {}.getType();
	private static final Path LIBRARY_PATH = Paths.get("tailslibrary.json");

	/**
	 * The list of library entries.
	 */
	public final List<LibraryEntryData> libraryEntries = new ArrayList<>();

	public LibraryManager() {}

	/**
	 * @return The {@link Gson} used for deserializing library entries.
	 */
	protected Gson getGson() {
		return Tails.SERVER_GSON;
	}

	/**
	 * Adds the given entries to the library.
	 * @param entries The entries to add.
	 */
	public void addEntries(List<? extends LibraryEntryData> entries) {
		libraryEntries.addAll(entries);
	}

	/**
	 * Adds a single entry to the library.
	 * @param data The entry to add.
	 */
	public void addEntry(LibraryEntryData data) {
		libraryEntries.add(data);
	}

	/**
	 * Removes the given entry from the library.
	 * @param data The entry to remove.
	 */
	public void removeEntry(LibraryEntryData data) {
		libraryEntries.remove(data);
	}

	/**
	 * Reloads the library from disk, possibly performing a backup.
	 * @param maybeBackup Whether to create a backup if the new library entries differ.
	 */
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
	 * Loads the library data from the file specified by {@link #createLibraryFile()}.
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
	 * Writes the current library data to the file specified by {@link #createLibraryFile()}.
	 */
	public void saveLibrary() {
		saveLibrary(createLibraryFile());
	}

	/**
	 * Saves the library data to the given file.
	 * @param to The file to write the data to.
	 */
	protected void saveLibrary(Path to) {
		try (BufferedWriter bw = Files.newBufferedWriter(to, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			getGson().toJson(libraryEntries, bw);
		} catch (IOException e) {
			Tails.LOGGER.error("Exception writing library:", e);
		}
	}

	/**
	 * Returns the path to the library file.
	 * By default, this is {@code tailslibrary.json} in the game directory.
	 * @return The library file.
	 */
	protected Path createLibraryFile() {
		return createFile(LIBRARY_PATH);
	}

	/**
	 * Creates a file if it doesn't exist.
	 * @param file The file to create.
	 * @return The created file.
	 */
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
