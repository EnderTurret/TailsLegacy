/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.part;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.api.IPlayerPartManager;

/**
 * The base implementation of {@link IPlayerPartManager}.
 * @author EnderTurret
 */
@Internal
public class PlayerPartManager implements IPlayerPartManager {

	protected final Map<UUID, PartsData> partsData = new HashMap<>();
	private final Map<UUID, PartsData> view = Collections.unmodifiableMap(partsData);

	@Override
	public boolean has(UUID uuid) {
		if (uuid == null) throw new NullPointerException();
		return partsData.containsKey(uuid);
	}

	@Override
	public PartsData get(UUID uuid) {
		if (uuid == null) throw new NullPointerException();
		return partsData.getOrDefault(uuid, PartsData.EMPTY);
	}

	@Override
	public Map<UUID, PartsData> getData() {
		return view;
	}

	@Override
	public PartsData remove(UUID uuid) {
		if (uuid == null) throw new NullPointerException();

		final PartsData ret = partsData.remove(uuid);
		return ret != null ? ret : PartsData.EMPTY;
	}

	@Override
	public PartsData set(UUID uuid, PartsData data) {
		if (uuid == null) throw new NullPointerException();
		if (data == null) throw new NullPointerException();

		final PartsData ret = data.isEmpty() ? partsData.remove(uuid) : partsData.put(uuid, data);
		return ret != null ? ret : PartsData.EMPTY;
	}

	@Override
	public void clear() {
		partsData.clear();
	}
}