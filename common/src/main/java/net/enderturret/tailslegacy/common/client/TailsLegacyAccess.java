/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.api.ITailsLegacySyncService;
import net.enderturret.tailslegacy.common.client.api.ITailsLegacyAccess;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.part.PartsData;

import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link ITailsLegacyAccess}.
 * @author EnderTurret
 */
@Internal
public final class TailsLegacyAccess implements ITailsLegacyAccess {

	private static TailsLegacyAccess instance;

	/**
	 * Creates (if necessary) the singleton {@link TailsLegacyAccess} instance and returns it.
	 * @return The {@link TailsLegacyAccess} instance.
	 */
	@Internal
	public static TailsLegacyAccess _get() {
		if (instance == null)
			instance = new TailsLegacyAccess();
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
	public void setSyncService(ITailsLegacySyncService service) {
		ClientPlayerPartManager.sync = service;
	}

	@Nullable
	@Override
	public ITailsLegacySyncService getSyncService() {
		return ClientPlayerPartManager.sync;
	}
}