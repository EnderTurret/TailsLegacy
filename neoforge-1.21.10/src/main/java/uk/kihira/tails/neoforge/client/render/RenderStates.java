/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.render;

import org.joml.Vector3f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;

/**
 * Provides an additional {@link RenderType} or two.
 * @author EnderTurret
 */
@EventBusSubscriber(modid = TailsPlatform.MOD_ID, value = Dist.CLIENT)
public final class RenderStates {

	/**
	 * Defines the first vector of the part preview diffuse lighting.
	 * @see RenderSystem#setShaderLights(GpuBufferSlice)
	 * @see Lighting
	 */
	public static final Vector3f PART_PREVIEW_DIFFUSE_LIGHTING_0 = new Vector3f(0, 0, 0);
	/**
	 * Defines the second vector of the part preview diffuse lighting.
	 * @see RenderSystem#setShaderLights(GpuBufferSlice)
	 * @see Lighting
	 */
	public static final Vector3f PART_PREVIEW_DIFFUSE_LIGHTING_1 = new Vector3f(0, 0, 1);

	private RenderStates() {}

	@SuppressWarnings("unchecked")
	private static final Class<? extends EntityRenderer<Avatar, AvatarRenderState>> RENDERER_CLASS = (Class<? extends EntityRenderer<Avatar, AvatarRenderState>>) AvatarRenderer.class;

	public static final ContextKey<TailsRenderData> RENDER_DATA = new ContextKey<>(ResourceLocation.fromNamespaceAndPath(TailsPlatform.MOD_ID, "render_data"));

	@SubscribeEvent
	static void registerStateModifiers(RegisterRenderStateModifiersEvent e) {
		e.registerEntityModifier(RENDERER_CLASS, RenderStates::addTailsRenderData);
	}

	public static void addTailsRenderData(Avatar entity, AvatarRenderState state) {
		final TailsRenderData data = new TailsRenderData();

		data.partsData = ClientPlayerPartManager.get().get(entity.getUUID());
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