/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.util.ResourceLocation;

import net.enderturret.tailslegacy.forge.common.TailsLegacy;
import net.enderturret.tailslegacy.forge.common.platform.ResourceManagerExtensions;

@Mixin(FolderResourcePack.class)
public abstract class MixinFolderResourcePack extends AbstractResourcePack implements ResourceManagerExtensions {

	private MixinFolderResourcePack() {
		super(null);
	}

	@Override
	public Collection<ResourceLocation> tailslegacy$listResources(String prefix, Predicate<ResourceLocation> filter) {
		final Path baseFile = resourcePackFile.toPath().resolve("assets").toAbsolutePath();

		final Set<ResourceLocation> ret = new HashSet<>();

		for (Object _domain : getResourceDomains()) {
			final String domain = (String) _domain;
			final Path domainPath = baseFile.resolve(domain);
			final Path start = domainPath.resolve(prefix);
			if (!Files.isDirectory(start)) continue;

			try (Stream<Path> stream = Files.walk(start)) {
				ret.addAll(stream
						.filter(Files::isRegularFile)
						.map(path -> {
							final String relative = domainPath.relativize(path.toAbsolutePath()).toString().replace('\\', '/');
							return new ResourceLocation(domain, relative);
						})
						.filter(filter)
						.collect(Collectors.toSet()));
			} catch (IOException e) {
				TailsLegacy.LOGGER.error("Exception listing resources of {}:", prefix, e);
			}
		}

		return ret;
	}
}