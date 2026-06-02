/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.gson.LibraryEntryDataSerializer;
import net.enderturret.tailslegacy.common.gson.LoggingExclusionStrategy;
import net.enderturret.tailslegacy.common.gson.client.ClientPartInfoSerializer;
import net.enderturret.tailslegacy.common.gson.client.ClientPartsDataSerializer;
import net.enderturret.tailslegacy.common.part.IPartInfo;
import net.enderturret.tailslegacy.common.part.PartsData;

/**
 * Manages the local part data, among other things.
 * @author EnderTurret
 */
@Internal
public final class LocalPartManager {

	/**
	 * The {@link Gson} configured for deserializing client-side part data.
	 */
	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setExclusionStrategies(new LoggingExclusionStrategy())
			.registerTypeHierarchyAdapter(PartsData.class, new ClientPartsDataSerializer())
			.registerTypeHierarchyAdapter(IPartInfo.class, new ClientPartInfoSerializer())
			.registerTypeAdapter(LibraryEntryData.class, new LibraryEntryDataSerializer())
			.create();

	private static ClientPartsData localPartsData = ClientPartsData.EMPTY;

	/**
	 * Reloads the local part data from the config.
	 */
	@Internal
	public static void reload() {
		// Load local player info.
		try {
			// Load player data.
			final String localPlayerOutfit = TailsClientPlatform.get().getConfigParts();

			// Load default if none exists.
			if (localPlayerOutfit == null || localPlayerOutfit.isEmpty())
				setLocalPartsData(new ClientPartsData());
			else
				localPartsData = (ClientPartsData) GSON.fromJson(localPlayerOutfit, PartsData.class);

			// Check if the data was upgraded, and write it back to the config if so.
			final String json = GSON.toJson(localPartsData);
			if (!json.equals(localPlayerOutfit))
				TailsClientPlatform.get().setConfigParts(json);
		} catch (Exception e) {
			TailsPlatform.get().logError("Failed to load local player data! Invalid data has been removed.", e);
		}
	}

	/**
	 * Sets the local part data both here and in the config.
	 * @param partsData The new part data.
	 */
	@Internal
	public static void setLocalPartsData(ClientPartsData partsData) {
		localPartsData = Objects.requireNonNull(partsData);
		TailsClientPlatform.get().setConfigParts(GSON.toJson(localPartsData));
	}

	@Internal
	public static void setLocalPartsDataFromEditorAndSync(ClientPartsData partsData) {
		setLocalPartsData(partsData);
		ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), partsData);

		syncToServer();
	}

	/**
	 * @return The local part data.
	 */
	@Internal
	public static ClientPartsData getLocalPartsData() {
		return localPartsData;
	}

	@Internal
	public static ClientPartsData getOrCreateLocalPartsData() {
		ClientPartsData data = getLocalPartsData();

		if (data == null)
			setLocalPartsData(data = new ClientPartsData());

		return data;
	}

	/**
	 * Syncs the local part data to the server and/or the sync service (if defined).
	 */
	@Internal
	public static void syncToServer() {
		final ClientPartsData partsData = getLocalPartsData();

		TailsClientPlatform.get().syncLocalToServer(partsData);

		if (ClientPlayerPartManager.sync != null) {
			final UUID localId = TailsClientPlatform.get().getLocalUUID();
			ClientPlayerPartManager.sync.upload(localId, partsData).exceptionally(e -> {
				TailsPlatform.get().logError("Exception uploading data for {} to sync service:", localId, e);
				return null;
			});
		}
	}
}