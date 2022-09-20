/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.client.TailsAccess;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.part.PartsData;

public interface ITailsAccess {

	public static ITailsAccess get() {
		return TailsAccess._get();
	}

	public PartsData getLocalData();
	public void setLocalData(PartsData data);

	public List<LibraryEntryData> getLibraryEntries();

	public PartsData getPartData(UUID uuid);
	public void setPartData(PartsData data, UUID uuid);

	public void setSyncService(ITailsSyncService service);
	@Nullable
	public ITailsSyncService getSyncService();
}