/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.render;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;

@EventBusSubscriber(modid = TailsPlatform.MOD_ID, value = Dist.CLIENT)
public final class RenderStates {

	private RenderStates() {}

	// The (Class) cast might seem redundant, but is necessary to satisfy javac.
	@SuppressWarnings({ "unchecked", "cast" })
	private static final Class<? extends EntityRenderer<Avatar, AvatarRenderState>> RENDERER_CLASS = (Class<? extends EntityRenderer<Avatar, AvatarRenderState>>) (Class) AvatarRenderer.class;

	public static final ContextKey<TailsRenderData> RENDER_DATA = new ContextKey<>(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "render_data"));

	@SubscribeEvent
	static void registerStateModifiers(RegisterRenderStateModifiersEvent e) {
		e.registerEntityModifier(RENDERER_CLASS, RenderStates::addTailsRenderData);
	}

	public static void addTailsRenderData(Avatar entity, AvatarRenderState state) {
		final TailsRenderData data = new TailsRenderData();

		data.partsData = ClientPlayerPartManager.get().get(entity.getUUID());
		for (ClientPartInfo part : data.partsData.getParts())
			if (part.getAnimatorStorage() != null)
				data.animatorStorage.put(part, part.getAnimatorStorage().copy());

		data.isFlying = entity instanceof Player player && player.getAbilities().flying && player.hasImpulse || entity.fallDistance > 1.5F;
		data.uuid = entity.getUUID();

		final ClientAvatarState avatarState = ((ClientAvatarEntity) entity).avatarState();
		data.cloakX = avatarState.getInterpolatedCloakX(state.partialTick);
		data.cloakY = avatarState.getInterpolatedCloakY(state.partialTick);
		data.cloakZ = avatarState.getInterpolatedCloakZ(state.partialTick);
		data.bob = avatarState.getInterpolatedBob(state.partialTick);
		data.walkDist = avatarState.getInterpolatedWalkDistance(state.partialTick);

		state.setRenderData(RENDER_DATA, data);
	}
}