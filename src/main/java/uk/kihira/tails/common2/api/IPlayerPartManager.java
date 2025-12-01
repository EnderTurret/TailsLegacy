/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.api;

import java.util.Map;
import java.util.UUID;

import uk.kihira.tails.common2.part.PartsData;

/**
 * Manages the data of all the players on either side.
 * On servers, this contains the data synced to and received from clients.
 * On clients, this contains the data used for rendering other players' tails/ears/parts.
 * @author EnderTurret
 */
public interface IPlayerPartManager {

	// Query operations

	/**
	 * @param uuid The {@link UUID}.
	 * @return {@code true} if there is part data for the given {@link UUID}.
	 * @throws NullPointerException If {@code uuid} is {@code null}.
	 */
	public boolean has(UUID uuid);

	/**
	 * Retrieves and returns the part data stored for the given {@link UUID}.
	 * If no such data exists, {@link PartsData#EMPTY} is returned.
	 * @param uuid The {@link UUID}.
	 * @return The {@link PartsData}.
	 * @throws NullPointerException If {@code uuid} is {@code null}.
	 */
	public PartsData get(UUID uuid);

	/**
	 * @return An immutable view of the stored part data.
	 */
	public Map<UUID, PartsData> getData();

	// Modification operations

	/**
	 * Removes the part data for the given {@link UUID}.
	 * @param uuid The {@link UUID}.
	 * @return The part data previously associated with the {@link UUID}, or {@link PartsData#EMPTY} if no such data existed.
	 * @throws NullPointerException If {@code uuid} is {@code null}.
	 */
	public PartsData remove(UUID uuid);

	/**
	 * Sets the part data for the given {@link UUID}.
	 * If {@code data} is {@linkplain PartsData#isEmpty() empty}, this behaves like {@link #remove(UUID)}.
	 * @param uuid The {@link UUID}.
	 * @param data The new part data.
	 * @return The part data previously associated with the {@link UUID}, or {@link PartsData#EMPTY} if no such data existed.
	 * @throws NullPointerException If {@code uuid} or {@code data} are {@code null}.
	 */
	public PartsData set(UUID uuid, PartsData data);

	/**
	 * Removes all stored part data.
	 */
	public void clear();
}