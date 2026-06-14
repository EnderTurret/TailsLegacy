/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.resources.SimpleResource;
import net.minecraft.util.ResourceLocation;

@Mixin(SimpleResource.class)
public interface SimpleResourceAccess {

	@Accessor("srResourceLocation")
	public ResourceLocation tails$srResourceLocation();
}