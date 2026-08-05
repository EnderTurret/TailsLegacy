/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;

import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;

public final class RenderStates {

	private RenderStates() {}

	public static final RenderStateDataKey<TailsRenderData> RENDER_DATA = RenderStateDataKey.create(() -> "tailslegacy:render_data");

	public static void addTailsRenderData(Avatar entity, AvatarRenderState state, float partialTick) {
		final TailsRenderData data = new TailsRenderData();

		data.partsData = ClientPlayerPartManager.get().get(entity.getUUID());
		for (ClientPartInfo part : data.partsData.getParts())
			if (part.getAnimatorStorage() != null)
				data.animatorStorage.put(part, part.getAnimatorStorage().copy());

		data.isFlying = entity instanceof Player player && player.getAbilities().flying || entity.fallDistance > 1.5F;
		data.uuid = entity.getUUID();

		final ClientAvatarState avatarState = ((ClientAvatarEntity) entity).avatarState();
		data.cloakX = avatarState.getInterpolatedCloakX(partialTick);
		data.cloakY = avatarState.getInterpolatedCloakY(partialTick);
		data.cloakZ = avatarState.getInterpolatedCloakZ(partialTick);
		data.bob = avatarState.getInterpolatedBob(partialTick);
		data.walkDist = avatarState.getInterpolatedWalkDistance(partialTick);
		data.partialTick = partialTick;

		state.setData(RENDER_DATA, data);
	}
}