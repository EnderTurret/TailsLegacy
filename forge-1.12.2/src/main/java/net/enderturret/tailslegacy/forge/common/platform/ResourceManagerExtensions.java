/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.platform;

import java.util.Collection;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

public interface ResourceManagerExtensions {

	public Collection<ResourceLocation> tailslegacy$listResources(String prefix, Predicate<ResourceLocation> filter);
}