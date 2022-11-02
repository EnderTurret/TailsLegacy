/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.PartRegistry;

@Internal
public final class FoxtatoRenderer {

	/*private FoxtatoFakeEntity fakeEntity;
	private final PartInfo tailPartInfo = new PartInfo(PartRegistry.FLUFFY_TAIL.getId(), 0, 0, new int[]{-5480951, -6594259, -5197647}, null);
	private final PartInfo earPartInfo = new PartInfo(PartRegistry.FOX_EARS.getId(), 0, 0, new int[]{-5480951, 0xFF000000, -5197647}, null);

	@SubscribeEvent
	public void onWorldUnload(LevelEvent.Unload e) {
		if (fakeEntity != null) {
			fakeEntity.remove(Entity.RemovalReason.DISCARDED);
			fakeEntity = null;
		}
	}

	@SubscribeEvent
	public void onPotatoRender(TinyPotatoRenderEvent e) {
		if (e.name.getString().equalsIgnoreCase("foxtato")) {
			if (fakeEntity == null) fakeEntity = new FoxtatoFakeEntity(Minecraft.getInstance().level);

			final PartRenderer foxTailRenderer = PartRenderRegistry.getRenderer(PartRegistry.FLUFFY_TAIL);
			final PartRenderer foxEarRenderer = PartRenderRegistry.getRenderer(PartRegistry.FOX_EARS);

			e.ms.pushPose();

			e.ms.scale(0.5F, 0.5F, 0.5F);

			e.ms.translate(0, 2F, 0.2F);

			foxTailRenderer.render(e.ms, fakeEntity, tailPartInfo, e.buffers, e.tile.getBlockPos().getX(), e.tile.getBlockPos().getY(), e.tile.getBlockPos().getZ(), e.partTicks, e.light, e.overlay, 1F, 1F, 1F, 1F);

			e.ms.translate(0, -0.7, -0.3F);
			e.ms.mulPose(Vector3f.YP.rotationDegrees(180));

			foxEarRenderer.render(e.ms, fakeEntity, earPartInfo, e.buffers, e.tile.getBlockPos().getX(), e.tile.getBlockPos().getY(), e.tile.getBlockPos().getZ(), e.partTicks, e.light, e.overlay, 1F, 1F, 1F, 1F);

			e.ms.popPose();

			RenderSystem.setShaderColor(1F, 0F, 1F, 1F);
		}
	}

	public static class FoxtatoFakeEntity extends FakeEntity {
		public FoxtatoFakeEntity(Level world) {
			super(world);
		}
	}*/
}