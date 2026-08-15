/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.gson.ResourceManagerWrapper;
import net.enderturret.tailslegacy.forge.common.TailsLegacy;

public final class ResourceManagerWrapperImpl implements ResourceManagerWrapper {

	public final IResourceManager manager;

	public ResourceManagerWrapperImpl(IResourceManager manager) {
		this.manager = manager;
	}

	@Override
	public JsonElement getJson(TResourceLocation path) {
		return readJson((ResourceLocation) path);
	}

	@Override
	public Map<TResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<TResourceLocation> filter) {
		@SuppressWarnings("unchecked")
		final Collection<ResourceLocation> resources = ((ResourceManagerExtensions) manager).tailslegacy$listResources(prefix, (Predicate) filter);
		final Map<TResourceLocation, JsonElement> ret = new TreeMap<>(TResourceLocation::t$compareTo);

		for (ResourceLocation rl : resources)
			ret.put((TResourceLocation) rl, readJson(rl));

		return ret;
	}

	/**
	 * Reads the given resource as json, catching any errors that may arise from doing so.
	 * @param location The location of the resource. Used for logging purposes.
	 * @param resource The resource itself.
	 * @return The parsed json, or {@code null} if an error occurred.
	 */
	@Nullable
	private JsonElement readJson(ResourceLocation location) {
		try (InputStream is = manager.getResource(location).getInputStream(); InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr)) {
			return new JsonParser().parse(br);
		} catch (Exception e) {
			// The stack trace might be increasingly large, so try not to log it.
			TailsLegacy.LOGGER.warn("Failed to read json file {}:\n{}", location, e.toString());
			return null;
		}
	}
}