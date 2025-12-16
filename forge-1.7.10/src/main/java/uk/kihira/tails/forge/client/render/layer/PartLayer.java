/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render.layer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.render.part.PartRenderer;
import uk.kihira.tails.forge.client.platform.TailsPoseStackImpl;
import uk.kihira.tails.forge.client.platform.TailsTessellatorWrapper;

/**
 * A {@code LayerRenderer} for Tails parts/accessories.
 */
public class PartLayer {

	public static void doRenderLayer(EntityLivingBase entity, float partialTick, String rootAttachment, ModelRenderer parentPart) {
		// Don't render a part if its root attachment isn't visible.
		// Prevents head parts rendering in first person in Sleep Tight beds, for example.
		if (!parentPart.showModel) return;

		final int originalTextureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		final ClientPartsData partsData = ClientPlayerPartManager.get().get(entity.getUniqueID());
		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (partInfo.isInvalid()) continue; // Skip unknown parts.
			if (!partInfo.getPart().getAttachment().root().id().equals(rootAttachment)) continue;

			final Part part = partInfo.getPart();
			final PartRenderer renderer = partInfo.getRenderer();

			GL11.glPushMatrix();

			parentPart.postRender(0.0625F);

			try {
				renderer.render(TailsPoseStackImpl.INSTANCE, (TailsEntity) entity, partsData, partInfo, TailsTessellatorWrapper.get(), 0, 0, 0, partialTick, 1, 1, 0xFF);
			} catch (Exception e) {
				TailsPlatform.get().logError("Exception rendering part {}: ", partInfo, e);
			}

			GL11.glPopMatrix();
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, originalTextureId);
	}
}