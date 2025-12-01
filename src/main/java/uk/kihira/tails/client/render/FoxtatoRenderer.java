/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.PartRegistry;

/**
 * Handles rendering Tails accessories on tiny potatoes named "foxtato" (case-insensitive).
 */
@Internal
public final class FoxtatoRenderer {

	private FakeEntity fakeEntity;

	@SubscribeEvent
	public void onWorldUnload(LevelEvent.Unload e) {
		if (fakeEntity != null) {
			fakeEntity.remove(Entity.RemovalReason.DISCARDED);
			fakeEntity = null;
		}
	}

	public void render(PoseStack poseStack, MultiBufferSource buffers, BlockPos pos, float partialTicks, int packedLight, int packedOverlay) {
		if (fakeEntity == null) fakeEntity = new FakeEntity(Minecraft.getInstance().level);

		final ClientPartInfo tailPartInfo = new ClientPartInfo(new int[]{-5480951, -6594259, -5197647}, PartRegistry.FLUFFY_TAIL);
		final ClientPartInfo earPartInfo = new ClientPartInfo(new int[]{-5480951, 0xFF000000, -5197647}, PartRegistry.FOX_EARS);

		final PartRenderer foxTailRenderer = tailPartInfo.getRenderer();
		final PartRenderer foxEarRenderer = earPartInfo.getRenderer();

		poseStack.pushPose();

		poseStack.scale(0.5F, 0.5F, 0.5F);

		poseStack.translate(0, 2F, 0.2F);

		foxTailRenderer.render(poseStack, fakeEntity, null, tailPartInfo, buffers, pos.getX(), pos.getY(), pos.getZ(), partialTicks, packedLight, packedOverlay, 0xFF);

		poseStack.translate(0, -0.7, -0.3F);
		poseStack.mulPose(new Quaternionf().rotateY(Mth.PI));

		foxEarRenderer.render(poseStack, fakeEntity, null, earPartInfo, buffers, pos.getX(), pos.getY(), pos.getZ(), partialTicks, packedLight, packedOverlay, 0xFF);

		poseStack.popPose();

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	/*@SubscribeEvent
	public void onPotatoRender(TinyPotatoRenderEvent e) {
		if (e.name.getString().equalsIgnoreCase("foxtato"))
			render(e.ms, e.buffers, e.tile.getBlockPos(), e.partTicks, e.light, e.overlay);
	}*/
}