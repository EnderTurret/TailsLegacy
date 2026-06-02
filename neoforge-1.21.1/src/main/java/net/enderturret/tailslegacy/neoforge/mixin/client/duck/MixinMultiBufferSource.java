/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;

@Mixin(MultiBufferSource.class)
@SuppressWarnings("cast")
public interface MixinMultiBufferSource extends TailsBufferSource {

	@Override
	public default TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture) {
		final ResourceLocation tex = (ResourceLocation) (Object) texture;
		boolean visible = true, visibleToPlayer = false, glowing = false;

		if (!entity.t$isPreview() && entity.t$unwrap() instanceof LivingEntity living) {
			visible = !living.isInvisible();
			visibleToPlayer = !visible && !living.isInvisibleTo(Minecraft.getInstance().player);
			glowing = Minecraft.getInstance().shouldEntityAppearGlowing(living);
		}

		final RenderType renderType;
		if (visibleToPlayer)
			renderType = RenderType.itemEntityTranslucentCull(tex);
		else if (visible)
			renderType = RenderType.entityCutoutNoCull(tex);
		else
			renderType = glowing ? RenderType.outline(tex) : null;

		return (TailsBuffer) ((MultiBufferSource) (Object) this).getBuffer(renderType);
	}
}