/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import uk.kihira.tails.api.ITailsSyncService;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.PlayerPartManager;

public class ClientPlayerPartManager extends PlayerPartManager {

	public static ITailsSyncService sync;

	private final Set<UUID> checked = new HashSet<>(0);

	private PartsData query(UUID uuid) {
		if (sync != null && checked.add(uuid)) {
			final PartsData data = Objects.requireNonNull(sync.query(uuid), "query() contract violated!");
			if (!data.isEmpty()) {
				set(uuid, data);
				return data;
			}
		}

		return PartsData.EMPTY;
	}

	private PartsData release(PartsData data) {
		data.clearTextures();
		return data;
	}

	@Override
	public boolean has(UUID uuid) {
		// TODO
		//if (LocalPartManager.getLocalPartsData() != null)
		//return true;

		final boolean has = super.has(uuid);

		// If we don't have data for that id, try querying the sync service for it.
		// If it resolves something, we can return true as we have now acquired the data.
		// This might block the client thread, but I'm sure it'll be fine.
		if (!has && !query(uuid).isEmpty())
			return true;

		return has;
	}

	@Override
	public PartsData get(UUID uuid) {
		// TODO
		//if (uuid == ClientUtils.getPlayerUUID())
		//return LocalPartManager.getLocalPartsData();

		final PartsData ret = super.get(uuid);
		return ret.isEmpty() ? query(uuid) : ret;
	}

	@Override
	public PartsData remove(UUID uuid) {
		checked.remove(uuid);
		return release(super.remove(uuid));
	}

	@Override
	public PartsData set(UUID uuid, PartsData data) {
		return release(super.set(uuid, data));
	}

	@Override
	public void clear() {
		checked.clear();
		getData().values().forEach(this::release);
		super.clear();
	}
}