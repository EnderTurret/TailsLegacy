/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.enderturret.tailslegacy.common.LibraryManager;
import net.enderturret.tailslegacy.common.client.api.PartRendererRegistrar;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.TailsPartDefinition;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.part.PartTexture;
import net.enderturret.tailslegacy.common.client.part.SubType;

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
				final Thread t = new Thread(r, "Tails Legacy Executor " + TailsClientInternal.THREAD_COUNT.getAndIncrement());
				t.setDaemon(true);
				return t;
			});

		return TailsClientInternal.executor;
	}

	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight);

	public boolean hasTexture(TResourceLocation id);
	public void registerTripleTintTexture(TResourceLocation id, Part part, SubType subType, PartTexture texture, int[] tints);
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