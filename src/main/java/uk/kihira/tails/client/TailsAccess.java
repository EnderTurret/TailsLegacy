/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.api.ITailsAccess;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.api.ITailsSyncService;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common2.part.PartsData;

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
	public PartsData getLocalData() {
		return LocalPartManager.getLocalPartsData();
	}

	@Override
	public void setLocalData(PartsData data) {
		if (!(data instanceof ClientPartsData))
			data = ClientPartsData.clone(data);

		LocalPartManager.setLocalPartsData((ClientPartsData) data);
		setPartData(data, ClientUtils.getPlayerUUID());
		LocalPartManager.syncToServer();
	}

	@Override
	public List<LibraryEntryData> getLibraryEntries() {
		return Tails.PROXY.getLibraryManager().libraryEntries;
	}

	@Override
	public PartsData getPartData(UUID uuid) {
		return ClientPlayerPartManager.get().get(uuid);
	}

	@Override
	public void setPartData(PartsData data, UUID uuid) {
		ClientPlayerPartManager.get().set(uuid, data);
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