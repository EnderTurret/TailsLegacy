/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.client.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.forge.common.Tails;
import uk.kihira.tails.forge.common.platform.ResourceManagerExtensions;

@Mixin(FallbackResourceManager.class)
public abstract class MixinFallbackResourceManager implements ResourceManagerExtensions {

	@Unique
	private static Set<Class> TAILS$IGNORED_CLASSES;

	@Shadow
	@Final
	private List<IResourcePack> resourcePacks;

	@Override
	public Collection<ResourceLocation> tails$listResources(String prefix, Predicate<ResourceLocation> filter) {
		if (TAILS$IGNORED_CLASSES == null) {
			TAILS$IGNORED_CLASSES = new HashSet<>();
			TAILS$IGNORED_CLASSES.add(DefaultResourcePack.class);
		}

		final Set<ResourceLocation> ret = new HashSet<>();

		for (IResourcePack resourcePack : resourcePacks)
			if (resourcePack instanceof ResourceManagerExtensions) {
				final Collection<ResourceLocation> collection = ((ResourceManagerExtensions) resourcePack).tails$listResources(prefix, filter);
				if (collection == null) throw new IllegalArgumentException("Resource pack " + resourcePack + " returned null for tails$listResources");
				ret.addAll(collection);
			} else if (TAILS$IGNORED_CLASSES.add(resourcePack.getClass()))
				Tails.LOGGER.warn("IResourcePack implementation " + resourcePack.getClass().getName() + " does not support Tails extensions; Tails data will not be loaded from it");

		return ret;
	}
}