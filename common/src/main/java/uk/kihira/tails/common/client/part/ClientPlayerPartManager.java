/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.part;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.api.ITailsSyncService;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.PlayerPartManager;

/**
 * The client-side implementation of the {@link PlayerPartManager}.
 * @author EnderTurret
 */
@Internal
public class ClientPlayerPartManager extends PlayerPartManager {

	/**
	 * The sync service. Will usually be {@code null}.
	 */
	@Internal
	@Nullable
	public static ITailsSyncService sync;

	private final Set<UUID> checked = new HashSet<>(0);

	/**
	 * Returns the {@link PlayerPartManager} used on the client.
	 * This should be perfectly safe as calling this method should mean you're on the client anyway.
	 * @return The client-side player part manager.
	 */
	public static ClientPlayerPartManager get() {
		return (ClientPlayerPartManager) TailsPlatform.get().getPartManager();
	}

	@Override
	protected ClientPartsData empty() {
		return ClientPartsData.EMPTY;
	}

	/**
	 * If a sync service is defined, queries it for data on the given id.
	 * Otherwise returns {@link PartsData#EMPTY}.
	 * @param uuid The id to query data for.
	 * @return The data.
	 */
	private ClientPartsData query(UUID uuid) {
		if (sync != null && checked.add(uuid)) {
			final ClientPartsData data = ClientPartsData.clone(Objects.requireNonNull(sync.query(uuid), "query() contract violated!"));
			if (!data.isEmpty()) {
				set(uuid, data);
				return data;
			}
		}

		return empty();
	}

	/**
	 * Performs any necessary cleanup on the given data, such as releasing native resources.
	 * @param data The data.
	 * @return The data.
	 */
	private ClientPartsData release(PartsData data) {
		data.clearTextures();
		return (ClientPartsData) data;
	}

	@Override
	public boolean has(UUID uuid) {
		final boolean has = super.has(uuid);

		// If we don't have data for that id, try querying the sync service for it.
		// If it resolves something, we can return true as we have now acquired the data.
		// This might block the client thread, but I'm sure it'll be fine.
		if (!has && !query(uuid).isEmpty())
			return true;

		return has;
	}

	@Override
	public ClientPartsData get(UUID uuid) {
		final ClientPartsData ret = (ClientPartsData) super.get(uuid);
		return ret.isEmpty() ? query(uuid) : ret;
	}

	@Override
	public ClientPartsData remove(UUID uuid) {
		checked.remove(uuid);
		return release(super.remove(uuid));
	}

	@Override
	public ClientPartsData set(UUID uuid, PartsData data) {
		Objects.requireNonNull(data);
		if (!(data instanceof ClientPartsData))
			data = ClientPartsData.clone(data);

		return release(super.set(uuid, data));
	}

	@Override
	public void clear() {
		checked.clear();
		getData().values().forEach(this::release);
		super.clear();
	}
}