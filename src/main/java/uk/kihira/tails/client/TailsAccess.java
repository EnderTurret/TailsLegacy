package uk.kihira.tails.client;

import java.util.List;
import java.util.UUID;

import uk.kihira.tails.api.ITailsAccess;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

public final class TailsAccess implements ITailsAccess {

	private static TailsAccess instance;

	public static TailsAccess _get() {
		if (instance == null)
			instance = new TailsAccess();
		return instance;
	}

	@Override
	public PartsData getLocalData() {
		return LocalPartManager.localPartsData;
	}

	@Override
	public void setLocalData(PartsData data) {
		LocalPartManager.setLocalPartsData(data);
		setPartData(data, ClientUtils.getPlayerUUID());
		LocalPartManager.syncToServer();
	}

	@Override
	public List<LibraryEntryData> getLibraryEntries() {
		return Tails.PROXY.getLibraryManager().libraryEntries;
	}

	@Override
	public PartsData getPartData(UUID uuid) {
		return Tails.PROXY.getPartsData(uuid);
	}

	@Override
	public void setPartData(PartsData data, UUID uuid) {
		Tails.PROXY.addPartsData(uuid, data);
	}
}