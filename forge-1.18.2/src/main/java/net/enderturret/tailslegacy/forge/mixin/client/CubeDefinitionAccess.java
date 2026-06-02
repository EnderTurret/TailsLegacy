/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;

@Mixin(CubeDefinition.class)
public interface CubeDefinitionAccess {

	@Invoker("<init>")
	public static CubeDefinition tails$new(
			@Nullable String comment,
			float texCoordU, float texCoordV,
			float originX, float originY, float originZ,
			float dimensionX, float dimensionY, float dimensionZ,
			CubeDeformation grow, boolean mirror,
			float texScaleU, float texScaleV) {
		return null;
	}
}