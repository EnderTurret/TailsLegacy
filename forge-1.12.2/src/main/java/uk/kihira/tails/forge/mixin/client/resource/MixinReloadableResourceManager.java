package uk.kihira.tails.forge.mixin.client.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.forge.common.Tails;
import uk.kihira.tails.forge.common.platform.ResourceManagerExtensions;

@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinReloadableResourceManager implements ResourceManagerExtensions {

	@Shadow
	@Final
	private Map<String, FallbackResourceManager> domainResourceManagers;

	@Override
	public Collection<ResourceLocation> tails$listResources(String prefix, Predicate<ResourceLocation> filter) {
		final Set<ResourceLocation> ret = new HashSet<>();

		for (FallbackResourceManager manager : domainResourceManagers.values()) {
			if (manager instanceof ResourceManagerExtensions)
				ret.addAll(((ResourceManagerExtensions) manager).tails$listResources(prefix, filter));
			else
				Tails.LOGGER.warn("Unknown resource manager type: {}", manager.getClass().getName());
		}

		return ret;
	}
}