/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.common.client.duck.TResourceLocation;

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
		return (TResourceLocation) (Object) ((ResourceLocation) (Object) this).withPath(path);
	}

	@Override
	public int t$compareTo(TResourceLocation other) {
		return ((ResourceLocation) (Object) this).compareTo((ResourceLocation) (Object) other);
	}

	@Override
	public int t$compareNamespaced(TResourceLocation other) {
		return ((ResourceLocation) (Object) this).compareNamespaced((ResourceLocation) (Object) other);
	}
}