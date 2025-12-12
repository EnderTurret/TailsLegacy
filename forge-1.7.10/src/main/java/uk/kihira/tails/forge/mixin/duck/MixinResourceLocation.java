/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.common.client.duck.TResourceLocation;

@Mixin(ResourceLocation.class)
public abstract class MixinResourceLocation implements TResourceLocation {

	@Override
	public String t$getNamespace() {
		return ((ResourceLocation) (Object) this).getResourceDomain();
	}

	@Override
	public String t$getPath() {
		return ((ResourceLocation) (Object) this).getResourcePath();
	}

	@Override
	public TResourceLocation t$withPath(String path) {
		final ResourceLocation rl = (ResourceLocation) (Object) this;
		return (TResourceLocation) new ResourceLocation(rl.getResourceDomain(), path);
	}

	@Override
	public int t$compareTo(TResourceLocation other) {
		final ResourceLocation self = (ResourceLocation) (Object) this;
		final ResourceLocation o = (ResourceLocation) other;

		int tmp = self.getResourceDomain().compareTo(o.getResourceDomain());
		if (tmp != 0) return tmp;

		return self.getResourcePath().compareTo(o.getResourcePath());
	}

	@Override
	public int t$compareNamespaced(TResourceLocation other) {
		return t$compareTo(other);
	}
}