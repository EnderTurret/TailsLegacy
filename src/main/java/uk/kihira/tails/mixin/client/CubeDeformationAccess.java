/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.geom.builders.CubeDeformation;

@Mixin(CubeDeformation.class)
public interface CubeDeformationAccess {

	@Accessor("growX")
	public float tails$growX();

	@Accessor("growY")
	public float tails$growY();

	@Accessor("growZ")
	public float tails$growZ();
}