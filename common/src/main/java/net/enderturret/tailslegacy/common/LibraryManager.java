/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

/**
 * As you may have guessed, this manages the Tails library.
 * It handles saving/loading the library as well as manipulating its entries.
 */
@Internal
public abstract class LibraryManager {

	private static final Path LIBRARY_PATH = Paths.get("tailslibrary.json");

	/**
	 * The list of library entries.
	 */
	public final List<LibraryEntryData> libraryEntries = new ArrayList<>();

	public LibraryManager() {}

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

	@Nullable
	protected abstract List<LibraryEntryData> readEntries();

	/**
	 * Loads the library data from the file specified by {@link #createLibraryFile()}.
	 * @return A list of loaded library data.
	 */
	private List<LibraryEntryData> loadLibrary() {
		final List<LibraryEntryData> libraryEntries = new ArrayList<>();

		if (Files.exists(LIBRARY_PATH)) {
			final List<LibraryEntryData> loadedEntries = readEntries();
			if (loadedEntries != null && !loadedEntries.isEmpty())
				for (LibraryEntryData libEntry : loadedEntries)
					if (libEntry.partsData != null)
						libraryEntries.add(libEntry);
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
	protected abstract void saveLibrary(Path to);

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
				TailsPlatform.get().logError("Failed to create library file!", e);
			}

		return file;
	}
}
