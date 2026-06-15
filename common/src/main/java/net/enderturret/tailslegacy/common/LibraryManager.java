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

	protected static final Path OLD_LIBRARY_PATH = Paths.get("tailslibrary.json");
	protected static final Path LIBRARY_PATH = Paths.get("tailslegacylibrary.json");

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
				saveLibrary(LIBRARY_PATH.resolveSibling("tailslegacylibrary.json.bak"));

		libraryEntries.clear();
		libraryEntries.addAll(entries);
	}

	@Nullable
	protected abstract List<LibraryEntryData> readEntries(Path from);

	/**
	 * Loads the library data from {@link #LIBRARY_PATH}.
	 * @return A list of loaded library data.
	 */
	private List<LibraryEntryData> loadLibrary() {
		final List<LibraryEntryData> libraryEntries = new ArrayList<>();

		Path path = null;

		if (Files.exists(LIBRARY_PATH))
			path = LIBRARY_PATH;
		else if (Files.exists(OLD_LIBRARY_PATH)) {
			path = OLD_LIBRARY_PATH;

			try {
				//"subType"
				final String contents = String.join("\n", Files.readAllLines(OLD_LIBRARY_PATH));
				if (contents.contains("\"subType\"") && contents.contains("\"textureId\"")) {
					TailsPlatform.get().logInfo("Migrating tailslibrary.json to tailslegacylibrary.json...");
					Files.move(OLD_LIBRARY_PATH, LIBRARY_PATH);
					path = LIBRARY_PATH;
				}
			} catch (Exception e) {
				TailsPlatform.get().logError("Exception migrating Tails library:", e);
			}
		}

		if (path == null) return libraryEntries;

		final List<LibraryEntryData> loadedEntries = readEntries(path);
		if (loadedEntries != null && !loadedEntries.isEmpty())
			for (LibraryEntryData libEntry : loadedEntries)
				if (libEntry.partsData != null)
					libraryEntries.add(libEntry);

		return libraryEntries;
	}

	/**
	 * Writes the current library data to {@link #LIBRARY_PATH}.
	 */
	public void saveLibrary() {
		saveLibrary(createFile(LIBRARY_PATH));
	}

	/**
	 * Saves the library data to the given file.
	 * @param to The file to write the data to.
	 */
	protected abstract void saveLibrary(Path to);

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
