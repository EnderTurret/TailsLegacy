/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common.platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.gson.ResourceManagerWrapper;
import uk.kihira.tails.forge.common.Tails;

public final class ResourceManagerWrapperImpl implements ResourceManagerWrapper {

	public final ResourceManager manager;

	public ResourceManagerWrapperImpl(ResourceManager manager) {
		this.manager = manager;
	}

	@Override
	public JsonElement getJson(TResourceLocation path) {
		return readJson((ResourceLocation) path);
	}

	@Override
	public Map<TResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<TResourceLocation> filter) {
		final Collection<ResourceLocation> resources = manager.listResources(prefix, str -> {
			final ResourceLocation rl = ResourceLocation.tryParse(str);
			return rl != null && filter.test((TResourceLocation) rl);
		});
		final Map<TResourceLocation, JsonElement> ret = new LinkedHashMap<>();

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
			return JsonParser.parseReader(br);
		} catch (Exception e) {
			// The stack trace might be increasingly large, so try not to log it.
			Tails.LOGGER.warn("Failed to read json file {}:\n{}", location, e.toString());
			return null;
		}
	}
}