/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.mixin.client.duck;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.neoforge.client.render.PreparedSubmitNodeCollector;

@Mixin(SubmitNodeStorage.class)
public class MixinSubmitNodeStorage implements TailsBufferSource {

	@Override
	public @Nullable TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture) {
		final SubmitNodeStorage storage = (SubmitNodeStorage) (Object) this;

		final Identifier tex = (Identifier) (Object) texture;
		boolean visible = true, visibleToPlayer = false, glowing = false;

		if (!entity.t$isPreview() && entity.t$unwrap() instanceof LivingEntity living) {
			visible = !living.isInvisible();
			visibleToPlayer = !visible && !living.isInvisibleTo(Minecraft.getInstance().player);
			glowing = Minecraft.getInstance().shouldEntityAppearGlowing(living);
		}

		final RenderType renderType;
		if (visibleToPlayer)
			renderType = RenderTypes.entityTranslucentCullItemTarget(tex);
		else if (visible)
			renderType = RenderTypes.entityCutout(tex);
		else
			renderType = glowing ? RenderTypes.outline(tex) : null;

		return renderType == null ? null : new PreparedSubmitNodeCollector(storage, renderType);
	}
}