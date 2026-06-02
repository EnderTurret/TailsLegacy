/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.gson;

import java.util.Map;
import java.util.function.Predicate;

import com.google.gson.JsonElement;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

public interface ResourceManagerWrapper {

	public JsonElement getJson(TResourceLocation path);
	public Map<TResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<TResourceLocation> filter);
}