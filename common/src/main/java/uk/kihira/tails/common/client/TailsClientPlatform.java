/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.TailsPartDefinition;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.Part;

public interface TailsClientPlatform {

	public static TailsClientPlatform get() {
		if (TailsClientInternal.platform == null)
			TailsClientInternal.platform = ServiceLoader.load(TailsClientPlatform.class)
					.iterator().next();

		return TailsClientInternal.platform;
	}

	public static ExecutorService getExecutor() {
		if (TailsClientInternal.executor == null)
			TailsClientInternal.executor = Executors.newFixedThreadPool(3, r -> {
				final Thread t = new Thread(r, "Tails Executor " + TailsClientInternal.THREAD_COUNT.getAndIncrement());
				t.setDaemon(true);
				return t;
			});

		return TailsClientInternal.executor;
	}

	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight);

	public boolean hasTexture(TResourceLocation id);
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints);
	public void releaseTexture(TResourceLocation id);

	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar);

	public String fetchUsername(UUID uuid);
	public UUID getLocalUUID();

	public String getConfigParts();
	public void setConfigParts(String json);
	public Path getConfigDir();
	public void syncLocalToServer(ClientPartsData partsData);

	public LibraryManager getLibraryManager();
}