/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.common.PartInfo;

public class FakeEntityRenderHelper implements IRenderHelper {

	@Override
	public void onPreRenderTail(MatrixStack matrixStack, LivingEntity entity, RenderPart tail, PartInfo info, double x, double y, double z) {
		switch (info.partType) {
		case TAIL: {
			//Nine tails
			if (info.typeid == 0 && info.subid == 2) {
				matrixStack.translate(0F, 0.85F, 0F);
			}
			else matrixStack.translate(0F, 0.65F, 0F);
			matrixStack.scale(0.9F, 0.9F, 0.9F);
			break;
		}
		// todo fake head using players skin?
		case MUZZLE:
			matrixStack.translate(0.2F, 1.25F, 0F);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-45F));
			matrixStack.rotate(Vector3f.XP.rotationDegrees(25F));
			break;
		case EARS: {
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
			matrixStack.translate(0F, 1.4F, 0F);
			break;
		}
		case WINGS: {
			matrixStack.translate(0F, 0.9F, 0F);
			matrixStack.scale(0.6F, 0.6F, 0.6F);
			break;
		}
		}
	}
}
