/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.mixin.client;

import java.util.Set;

import javax.annotation.Nullable;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;

@Mixin(CubeDefinition.class)
public interface CubeDefinitionAccess {

	@Invoker("<init>")
	public static CubeDefinition tails$new(
			@Nullable String comment,
			float texCoordU, float texCoordV,
			float originX, float originY, float originZ,
			float dimensionX, float dimensionY, float dimensionZ,
			CubeDeformation grow, boolean mirror,
			float texScaleU, float texScaleV,
			Set<Direction> visibleFaces) {
		return null;
	}

	@Accessor("comment")
	public String tails$comment();

	@Accessor("origin")
	public Vector3f tails$origin();

	@Accessor("dimensions")
	public Vector3f tails$dimensions();

	@Accessor("grow")
	public CubeDeformation tails$grow();

	@Accessor("mirror")
	public boolean tails$mirror();

	@Accessor("texCoord")
	public UVPair tails$texCoord();

	//@Accessor("texScale")
	//public UVPair tails$texScale();
}