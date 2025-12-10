package uk.kihira.tails.forge.common.platform;

import java.util.Collection;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

public interface ResourceManagerExtensions {

	public Collection<ResourceLocation> tails$listResources(String prefix, Predicate<ResourceLocation> filter);
}