/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.core.Direction;

import uk.kihira.tails.forge.client.render.ModelPartCubeExtensions;

@Mixin(CubeDefinition.class)
public abstract class MixinCubeDefinition implements ModelPartCubeExtensions {

	@Unique
	private Collection<Direction> tails$faces;

	@Override
	public void tails$setHiddenFaces(Collection<Direction> faces) {
		tails$faces = faces;
	}

	@Inject(at = @At("RETURN"), method = "bake")
	private void tails$setHiddenFacesOnCube(CallbackInfoReturnable<ModelPart.Cube> cir) {
		if (tails$faces != null)
			((ModelPartCubeExtensions) cir.getReturnValue()).tails$setHiddenFaces(tails$faces);
	}
}