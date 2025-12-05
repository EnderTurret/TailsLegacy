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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.neoforge.client.render.PreparedSubmitNodeStorage;

@Mixin(SubmitNodeStorage.class)
public class MixinSubmitNodeStorage implements TailsBufferSource {

	@Override
	public TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture) {
		final SubmitNodeStorage storage = (SubmitNodeStorage) (Object) this;

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

		return new PreparedSubmitNodeStorage(storage, renderType);
	}
}