/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render.layer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.render.layer.BasePartLayer;
import uk.kihira.tails.forge.client.platform.TailsPoseStackImpl;
import uk.kihira.tails.forge.client.platform.TailsTessellatorWrapper;

/**
 * A {@link LayerRenderer} for Tails parts/accessories.
 * @param <T> The entity type.
 */
public class PartLayer<T extends EntityLivingBase> implements LayerRenderer<T>, BasePartLayer {

	protected final RenderPlayer renderer;

	public PartLayer(RenderPlayer renderer) {
		this.renderer = renderer;
	}

	@Override
	@Nullable
	public TailsModelPart attachmentPart(String attachmentRoot) {
		switch (attachmentRoot) {
			case "head": return (TailsModelPart) renderer.modelBipedMain.bipedHead;
			case "body": return (TailsModelPart) renderer.modelBipedMain.bipedBody;
			default: return null;
		}
	}

	@Override
	public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// Vanilla does a translate() call instead of posing the ModelRenderers here,
		// so we need to copy that here.
		// TODO: Should we grab the matrix when the ModelRenderers are rendered and use those instead? (Might be more mod compatible.)
		final boolean crouching = entity instanceof EntityPlayer && entity.isSneaking();
		if (crouching) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0.2F, 0);
		}

		renderParts(
				(TailsEntity) entity,
				TailsPoseStackImpl.INSTANCE,
				TailsTessellatorWrapper.get(),
				partialTick,
				1,
				1
				);

		if (crouching)
			GL11.glPopMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}