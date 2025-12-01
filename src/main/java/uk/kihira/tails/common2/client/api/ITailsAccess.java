/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.api;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.api.ITailsSyncService;
import uk.kihira.tails.common2.client.TailsAccess;
import uk.kihira.tails.common2.part.PartsData;

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
	public PartsData getLocalData();
	/**
	 * Sets the local part data, updates the config, and syncs the data to servers.
	 * @param data The new part data.
	 */
	public void setLocalData(PartsData data);

	/**
	 * @return The library entries.
	 */
	public List<LibraryEntryData> getLibraryEntries();

	/**
	 * Retrieves the part data associated with the given {@link UUID}.
	 * @param uuid The id of the part data to retrieve.
	 * @return The part data, or {@link PartsData#EMPTY} if no such part data exists.
	 */
	public PartsData getPartData(UUID uuid);
	/**
	 * Sets the part data associated with the given {@link UUID} to the given part data.
	 * @param data The new data.
	 * @param uuid The id of the part data.
	 */
	public void setPartData(PartsData data, UUID uuid);

	/**
	 * Sets the sync service implementation in use.
	 * @param service The new service implementation.
	 */
	public void setSyncService(ITailsSyncService service);
	/**
	 * @return The sync service implementation, or {@code null} if no such implementation is installed.
	 */
	@Nullable
	public ITailsSyncService getSyncService();
}