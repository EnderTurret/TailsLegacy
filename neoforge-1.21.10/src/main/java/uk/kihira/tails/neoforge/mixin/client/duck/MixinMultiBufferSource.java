/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;

@Mixin(SubmitNodeStorage.class)
public interface MixinMultiBufferSource extends TailsBufferSource, TailsBuffer {

	@Override
	public default TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture) {
		final SubmitNodeStorage storage = (SubmitNodeStorage) this;
		storage.submitCustomGeometry(null, null, null);
		return this;
	}

	
}