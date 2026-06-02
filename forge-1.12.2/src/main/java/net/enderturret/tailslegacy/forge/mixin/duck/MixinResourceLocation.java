/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.ResourceLocation;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

@Mixin(ResourceLocation.class)
public abstract class MixinResourceLocation implements TResourceLocation {

	@Override
	public String t$getNamespace() {
		return ((ResourceLocation) (Object) this).getNamespace();
	}

	@Override
	public String t$getPath() {
		return ((ResourceLocation) (Object) this).getPath();
	}

	@Override
	public TResourceLocation t$withPath(String path) {
		final ResourceLocation rl = (ResourceLocation) (Object) this;
		return (TResourceLocation) new ResourceLocation(rl.getNamespace(), path);
	}

	@Override
	public int t$compareTo(TResourceLocation other) {
		return ((ResourceLocation) (Object) this).compareTo((ResourceLocation) other);
	}

	@Override
	public int t$compareNamespaced(TResourceLocation other) {
		return ((ResourceLocation) (Object) this).compareTo((ResourceLocation) other);
	}
}