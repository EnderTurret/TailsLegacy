/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common.platform;

import java.util.Collection;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

public interface ResourceManagerExtensions {

	public Collection<ResourceLocation> tails$listResources(String prefix, Predicate<ResourceLocation> filter);
}