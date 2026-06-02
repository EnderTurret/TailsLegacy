/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.api.ITailsSyncService;
import net.enderturret.tailslegacy.common.api.impl.SimpleLocalTailsSyncService;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.part.PartsData;
import net.enderturret.tailslegacy.common.part.PlayerPartManager;

import org.jetbrains.annotations.Nullable;

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

	private static ClientPlayerPartManager partManager;

	/**
	 * Returns the {@link PlayerPartManager} used on the client.
	 * This should be perfectly safe as calling this method should mean you're on the client anyway.
	 * @return The client-side player part manager.
	 */
	public static ClientPlayerPartManager get() {
		if (partManager == null) {
			if (sync == null && Boolean.getBoolean("tailslegacy.local-sync.enabled")) {
				TailsPlatform.get().logInfo("Enabling local sync service (as requested by 'tailslegacy.local-sync.enabled')...");
				sync = SimpleLocalTailsSyncService.fromConfigDir(TailsClientPlatform.get().getConfigDir());
			}

			partManager = new ClientPlayerPartManager();
		}
		return partManager;
	}

	public static void releaseAnimatorStorages() {
		if (partManager == null) return;

		for (PartsData data : partManager.partsData.values())
			if (data instanceof ClientPartsData)
				for (ClientPartInfo cpi : ((ClientPartsData) data).getParts())
					cpi.setAnimatorStorage(null);

		// In case for whatever reason the local data isn't in the part manager:
		if (LocalPartManager.getLocalPartsData() != null)
			for (ClientPartInfo cpi : LocalPartManager.getLocalPartsData().getParts())
				cpi.setAnimatorStorage(null);
	}

	private final Map<UUID, List<ClientPartInfo>> tickingParts = new ConcurrentHashMap<>(); // Make this concurrent in case Netty threads mess with it.

	public void tick(Collection<TailsEntity> players) {
		for (TailsEntity entity : players) {
			// On 1.7.10 (and possibly newer versions?), Minecraft will simply not remove dead players from the player list.
			// This previously caused animator ticking to be called multiple times, since multiple copies of the player were in the list.
			// We try to mitigate this by skipping players that are dead or not actually in the world.
			if (!entity.t$isAddedToWorld() || entity.t$isDead()) continue;

			final List<ClientPartInfo> tickables = tickingParts.get(entity.t$uuid());
			if (tickables == null) continue;

			for (ClientPartInfo part : tickables)
				try {
					part.setAnimatorStorage(part.getPart().getAnimation().tick(part.getAnimatorStorage(), entity));
				} catch (Exception e) {
					TailsPlatform.get().logError("Exception ticking animator:", e);
				}
		}
	}

	@Override
	protected ClientPartsData empty() {
		return ClientPartsData.EMPTY;
	}

	/**
	 * If a sync service is defined, queries it for data on the given id.
	 * @param uuid The id to query data for.
	 */
	private void query(UUID uuid) {
		if (sync != null && checked.add(uuid))
			sync.query(uuid).thenAcceptAsync(data -> {
				Objects.requireNonNull(data, "query() contract violated!");
				if (!data.isEmpty()) set(uuid, ClientPartsData.clone(data));
			}, TailsClientPlatform.getExecutor()).exceptionally(e -> {
				TailsPlatform.get().logError("Exception querying sync service for {}:", uuid, e);
				return null;
			});
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
		if (!has) query(uuid);

		return has;
	}

	@Override
	public ClientPartsData get(UUID uuid) {
		final ClientPartsData ret = (ClientPartsData) super.get(uuid);
		if (ret.isEmpty()) query(uuid);
		return ret;
	}

	@Override
	public ClientPartsData remove(UUID uuid) {
		checked.remove(uuid);
		tickingParts.remove(uuid);
		return release(super.remove(uuid));
	}

	@Override
	public ClientPartsData set(UUID uuid, PartsData data) {
		Objects.requireNonNull(data);
		if (!(data instanceof ClientPartsData))
			data = ClientPartsData.clone(data);

		final List<ClientPartInfo> tickables = ((ClientPartsData) data).getParts().stream()
				.filter(cpi -> !cpi.isInvalid() && cpi.getPart().getAnimation() != null && cpi.getPart().getAnimation().isTicking())
				.collect(Collectors.toList());

		if (!tickables.isEmpty())
			tickingParts.put(uuid, tickables);
		else
			tickingParts.remove(uuid);

		return release(super.set(uuid, data));
	}

	@Override
	public void clear() {
		checked.clear();
		tickingParts.clear();
		getData().values().forEach(this::release);
		super.clear();
	}
}