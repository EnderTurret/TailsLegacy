/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.mixin.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.resources.Identifier;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

@Mixin(Identifier.class)
public abstract class MixinIdentifier implements TResourceLocation {

	@Override
	public String t$getNamespace() {
		return ((Identifier) (Object) this).getNamespace();
	}

	@Override
	public String t$getPath() {
		return ((Identifier) (Object) this).getPath();
	}

	@Override
	public TResourceLocation t$withPath(String path) {
		return (TResourceLocation) (Object) ((Identifier) (Object) this).withPath(path);
	}

	@Override
	public int t$compareTo(TResourceLocation other) {
		return ((Identifier) (Object) this).compareTo((Identifier) (Object) other);
	}

	@Override
	public int t$compareNamespaced(TResourceLocation other) {
		return ((Identifier) (Object) this).compareNamespaced((Identifier) (Object) other);
	}
}