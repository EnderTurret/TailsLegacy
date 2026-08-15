/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client.resource;

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

import net.enderturret.tailslegacy.forge.common.TailsLegacy;
import net.enderturret.tailslegacy.forge.common.platform.ResourceManagerExtensions;

@Mixin(FallbackResourceManager.class)
public abstract class MixinFallbackResourceManager implements ResourceManagerExtensions {

	@Unique
	private static Set<Class> TAILSLEGACY$IGNORED_CLASSES;

	@Shadow
	@Final
	private List<IResourcePack> resourcePacks;

	@Override
	public Collection<ResourceLocation> tailslegacy$listResources(String prefix, Predicate<ResourceLocation> filter) {
		if (TAILSLEGACY$IGNORED_CLASSES == null) {
			TAILSLEGACY$IGNORED_CLASSES = new HashSet<>();
			TAILSLEGACY$IGNORED_CLASSES.add(DefaultResourcePack.class);
		}

		final Set<ResourceLocation> ret = new HashSet<>();

		for (IResourcePack resourcePack : resourcePacks)
			if (resourcePack instanceof ResourceManagerExtensions) {
				final Collection<ResourceLocation> collection = ((ResourceManagerExtensions) resourcePack).tailslegacy$listResources(prefix, filter);
				if (collection == null) throw new IllegalArgumentException("Resource pack " + resourcePack + " returned null for tailslegacy$listResources");
				ret.addAll(collection);
			} else if (TAILSLEGACY$IGNORED_CLASSES.add(resourcePack.getClass()))
				switch (resourcePack.getClass().getName()) {
					case "ganymedes01.etfuturum.client.BuiltInResourcePack$BuiltInFileResourcePack":
					case "ganymedes01.etfuturum.client.GrayscaleWaterResourcePack":
					case "ganymedes01.etfuturum.client.DynamicSoundsResourcePack":
					case "makamys.mclib.ext.assetdirector.mc.MultiVersionDefaultResourcePack": break;
					default: TailsLegacy.LOGGER.warn("IResourcePack implementation " + resourcePack.getClass().getName() + " does not support Tails extensions; Tails data will not be loaded from it");
				}

		return ret;
	}
}