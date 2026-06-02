/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.api;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.api.ITailsSyncService;
import net.enderturret.tailslegacy.common.client.TailsAccess;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.part.PartsData;

/**
 * Allows accessing certain Tails internals without falling victim to breaking changes to internal non-API.
 * @author EnderTurret
 */
public interface ITailsAccess {

	/**
	 * @return The {@link ITailsAccess} implementation.
	 */
	public static ITailsAccess get() {
		return TailsAccess._get();
	}

	/**
	 * @return The local part data.
	 */
	public ClientPartsData getLocalData();

	/**
	 * Sets the local part data, updates the config, and syncs the data to server.
	 * @param data The new part data.
	 * @see #setLocalData(ClientPartsData, boolean)
	 */
	public default void setLocalData(ClientPartsData data) { setLocalData(data, true); }

	/**
	 * Sets the local part data, updates the config, and optionally syncs the new data to the server.
	 * @param data The new part data.
	 * @param syncToServer Whether or not to sync the new data to the server.
	 */
	public void setLocalData(ClientPartsData data, boolean syncToServer);

	/**
	 * Returns a list consisting of the player's library entries.
	 * @return The library entries.
	 */
	public List<LibraryEntryData> getLibraryEntries();

	/**
	 * Returns the library entry with the specified name, or {@code null} if one doesn't exist.
	 * @param name The name of the desired library entry.
	 * @return The library entry with the specified name, or {@code null}.
	 */
	public @Nullable LibraryEntryData getLibraryEntryByName(String name);

	/**
	 * <p>
	 * Returns the library entry that would be selected if the player opened the library section of the editor.
	 * </p>
	 * <p>
	 * <b>Note:</b> Library entries do not have a 'selected' state — this just returns the first library entry that precisely matches the player's parts.
	 * </p>
	 * @return The 'selected' library entry, or {@code null} if none match.
	 */
	public @Nullable LibraryEntryData getSelectedLibraryEntry();

	/**
	 * Retrieves the part data associated with the given {@link UUID}.
	 * @param uuid The id of the part data to retrieve.
	 * @return The part data, or {@link PartsData#EMPTY} if no such part data exists.
	 */
	public ClientPartsData getPartData(UUID uuid);

	/**
	 * Sets the part data associated with the given {@link UUID} to the given part data.
	 * @param uuid The id of the part data.
	 * @param data The new data.
	 */
	public void setPartData(UUID uuid, ClientPartsData data);

	/**
	 * Sets the sync service implementation Tails should use to query the part data of players when the server does not have the mod installed.
	 * @param service The new sync service implementation.
	 */
	public void setSyncService(ITailsSyncService service);

	/**
	 * @return The sync service implementation, or {@code null} if no such implementation is installed.
	 */
	public @Nullable ITailsSyncService getSyncService();
}