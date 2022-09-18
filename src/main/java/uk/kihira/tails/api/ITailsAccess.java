package uk.kihira.tails.api;

import java.util.List;
import java.util.UUID;

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
}