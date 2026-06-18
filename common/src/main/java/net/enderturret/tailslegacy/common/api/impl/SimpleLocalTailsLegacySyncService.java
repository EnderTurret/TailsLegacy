/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.api.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.api.ITailsLegacySyncService;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;
import net.enderturret.tailslegacy.common.part.PartsData;

/**
 * An implementation of {@link ITailsLegacySyncService} that uses a flat directory containing json files.
 * This implementation is suitable for use among a small group of known people, as past that the setup gets unwieldy.
 *
 * @author EnderTurret
 */
public final class SimpleLocalTailsLegacySyncService implements ITailsLegacySyncService {

	private static final boolean DEBUG = Boolean.getBoolean("tailslegacy.local-sync.debug");

	private final Path dir;

	public SimpleLocalTailsLegacySyncService(Path dir) {
		this.dir = dir;

		if (!Files.exists(dir))
			try {
				Files.createDirectories(dir);
			} catch (IOException e) {
				TailsPlatform.get().logError("[Local Sync] Exception setting up sync directory at {}:", dir.toAbsolutePath(), e);
			}
	}

	public static SimpleLocalTailsLegacySyncService fromConfigDir(Path configDir) {
		return new SimpleLocalTailsLegacySyncService(configDir.resolve("tailslegacy-sync"));
	}

	@Override
	public CompletableFuture<Void> upload(UUID uuid, PartsData data) {
		return CompletableFuture.runAsync(() -> {
			final Path file = dir.resolve(uuid + ".json");

			if (DEBUG) TailsPlatform.get().logInfo("[Local Sync] Writing part data for {} ({}).", uuid, file.getFileName());

			final String json = TailsGsonHelper.SERVER_GSON.toJson(data);

			try (BufferedWriter bw = Files.newBufferedWriter(file)) {
				bw.write(json);
			} catch (Exception e) {
				TailsPlatform.get().logError("[Local Sync] Exception writing part data for {}:", uuid, e);
			}
		}, TailsClientPlatform.getExecutor());
	}

	@Override
	public CompletableFuture<PartsData> query(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			final Path file = dir.resolve(uuid + ".json");
			if (Files.isRegularFile(file))
				try (BufferedReader br = Files.newBufferedReader(file)) {
					return TailsGsonHelper.SERVER_GSON.fromJson(br, PartsData.class);
				} catch (Exception e) {
					TailsPlatform.get().logError("[Local Sync] Exception reading part data for {}:", uuid, e);
				}

			if (DEBUG) TailsPlatform.get().logInfo("[Local Sync] No part data found for {} ({}).", uuid, file.getFileName());

			return PartsData.EMPTY;
		}, TailsClientPlatform.getExecutor());
	}
}