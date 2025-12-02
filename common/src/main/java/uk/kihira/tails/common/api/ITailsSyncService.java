/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.api;

import java.util.UUID;

import uk.kihira.tails.common.part.PartsData;

/**
 * An interface that can be implemented to provide a syncing service for player tails.
 * If set, this can be used when server-side syncing is not possible.
 */
public interface ITailsSyncService {

	/**
	 * Uploads the given {@link PartsData} for the given {@link UUID}.
	 * This method may validate that the {@code UUID} belongs to the player, to prevent people from changing other people's data.
	 * @param uuid The {@code UUID} to upload the data for.
	 * @param data The data to upload.
	 */
	public void upload(UUID uuid, PartsData data);

	/**
	 * Queries the sync service for part data for the given {@link UUID}.
	 * @param uuid The {@link UUID} to query part data for.
	 * @return The part data for the specified {@link UUID}, or {@link PartsData#EMPTY} if the sync service does not have any data for it.
	 */
	public PartsData query(UUID uuid);
}