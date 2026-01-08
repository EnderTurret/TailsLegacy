/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.api.ITailsSyncService;
import uk.kihira.tails.common.client.api.ITailsAccess;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.client.part.LocalPartManager;
import uk.kihira.tails.common.part.PartsData;

/**
 * Implementation of {@link ITailsAccess}.
 * @author EnderTurret
 */
@Internal
public final class TailsAccess implements ITailsAccess {

	private static TailsAccess instance;

	/**
	 * Creates (if necessary) the singleton {@link TailsAccess} instance and returns it.
	 * @return The {@link TailsAccess} instance.
	 */
	@Internal
	public static TailsAccess _get() {
		if (instance == null)
			instance = new TailsAccess();
		return instance;
	}

	@Override
	public ClientPartsData getLocalData() {
		return LocalPartManager.getLocalPartsData();
	}

	@Override
	public void setLocalData(ClientPartsData data, boolean syncToServer) {
		if (getLocalData().equals(data)) return;

		data = data.deepCopy();

		LocalPartManager.setLocalPartsData(data);
		ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), data);
		if (syncToServer) LocalPartManager.syncToServer();
	}

	@Override
	public List<LibraryEntryData> getLibraryEntries() {
		return TailsClientPlatform.get().getLibraryManager().libraryEntries;
	}

	@Override
	public @Nullable LibraryEntryData getLibraryEntryByName(String name) {
		for (LibraryEntryData entry : getLibraryEntries())
			if (entry.entryName.equals(name))
				return entry;

		return null;
	}

	@Override
	public @Nullable LibraryEntryData getSelectedLibraryEntry() {
		final PartsData parts = getLocalData();

		for (LibraryEntryData entry : getLibraryEntries())
			if (entry.partsData.equals(parts))
				return entry;

		return null;
	}

	@Override
	public ClientPartsData getPartData(UUID uuid) {
		return ClientPlayerPartManager.get().get(uuid);
	}

	@Override
	public void setPartData(UUID uuid, ClientPartsData data) {
		ClientPlayerPartManager.get().set(uuid, data.deepCopy());
	}

	@Override
	public void setSyncService(ITailsSyncService service) {
		ClientPlayerPartManager.sync = service;
	}

	@Nullable
	@Override
	public ITailsSyncService getSyncService() {
		return ClientPlayerPartManager.sync;
	}
}