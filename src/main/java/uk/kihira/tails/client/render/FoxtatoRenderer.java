/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;
import vazkii.botania.api.item.TinyPotatoRenderEvent;

public class FoxtatoRenderer {

	private FoxtatoFakeEntity fakeEntity;
	private final PartInfo tailPartInfo = new PartInfo(0, 0, 0, new int[]{-5480951, -6594259, -5197647}, PartType.TAIL, null);
	private final PartInfo earPartInfo = new PartInfo(0, 0, 0, new int[]{-5480951, 0xFF000000, -5197647}, PartType.EARS, null);

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e) {
		if (fakeEntity != null) {
			fakeEntity.remove();
			fakeEntity = null;
		}
	}

	@SubscribeEvent
	public void onPotatoRender(TinyPotatoRenderEvent e) {
		if (e.name.getString().equalsIgnoreCase("foxtato")) {
			if (fakeEntity == null) fakeEntity = new FoxtatoFakeEntity(Minecraft.getInstance().world);

			final PartRenderer foxTailRenderer = PartRegistry.getPartRenderer(PartType.TAIL, 0);
			final PartRenderer foxEarRenderer = PartRegistry.getPartRenderer(PartType.EARS, 0);

			e.ms.push();

			e.ms.scale(0.5F, 0.5F, 0.5F);

			e.ms.translate(0, 2F, 0.2F);

			foxTailRenderer.render(e.ms, fakeEntity, tailPartInfo, e.buffers, e.tile.getPos().getX(), e.tile.getPos().getY(), e.tile.getPos().getZ(), e.partTicks, e.light, e.overlay, 1F, 1F, 1F, 1F);

			e.ms.translate(0, -0.7, -0.3F);
			e.ms.rotate(Vector3f.YP.rotationDegrees(180));

			foxEarRenderer.render(e.ms, fakeEntity, earPartInfo, e.buffers, e.tile.getPos().getX(), e.tile.getPos().getY(), e.tile.getPos().getZ(), e.partTicks, e.light, e.overlay, 1F, 1F, 1F, 1F);

			e.ms.pop();

			RenderSystem.color4f(1F, 0F, 1F, 1F);
		}
	}

	public static class FoxtatoFakeEntity extends FakeEntity {
		public FoxtatoFakeEntity(World world) {
			super(world);
		}
	}
}