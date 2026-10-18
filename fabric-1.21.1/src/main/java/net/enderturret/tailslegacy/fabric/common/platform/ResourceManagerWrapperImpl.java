/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common.platform;

import java.io.BufferedReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.gson.ResourceManagerWrapper;
import net.enderturret.tailslegacy.fabric.common.TailsLegacy;

public final class ResourceManagerWrapperImpl implements ResourceManagerWrapper {

	public final ResourceManager manager;

	public ResourceManagerWrapperImpl(ResourceManager manager) {
		this.manager = manager;
	}

	@Override
	public JsonElement getJson(TResourceLocation path) {
		final Identifier rl = (Identifier) (Object) path;
		return readJson(rl, manager.getResource(rl).get());
	}

	@Override
	public Map<TResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<TResourceLocation> filter) {
		@SuppressWarnings("unchecked")
		final Map<Identifier, Resource> map = manager.listResources(prefix, (Predicate) filter);
		final Map<TResourceLocation, JsonElement> ret = new LinkedHashMap<>();

		for (var entry : map.entrySet())
			ret.put((TResourceLocation) (Object) entry.getKey(), readJson(entry.getKey(), entry.getValue()));

		return ret;
	}

	/**
	 * Reads the given resource as json, catching any errors that may arise from doing so.
	 * @param location The location of the resource. Used for logging purposes.
	 * @param resource The resource itself.
	 * @return The parsed json, or {@code null} if an error occurred.
	 */
	@Nullable
	private static JsonElement readJson(Identifier location, Resource resource) {
		try (BufferedReader br = resource.openAsReader()) {
			return JsonParser.parseReader(br);
		} catch (Exception e) {
			// The stack trace might be increasingly large, so try not to log it.
			TailsLegacy.LOGGER.warn("Failed to read json file {}:\n{}", location, e.toString());
			return null;
		}
	}
}