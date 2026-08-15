/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@Mixin(PartDefinition.class)
public interface PartDefinitionAccess {

	@Invoker("<init>")
	public static PartDefinition tailslegacy$new(List<CubeDefinition> cubes, PartPose partPose) {
		return null;
	}

	@Accessor("children")
	public Map<String, PartDefinition> tailslegacy$children();
}