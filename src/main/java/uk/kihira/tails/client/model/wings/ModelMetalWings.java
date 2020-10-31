/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.wings;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.tails.client.model.ModelPartBase;

public class ModelMetalWings extends ModelPartBase {

	final ModelRenderer wing;

	public ModelMetalWings() {
		textureWidth = 64;
		textureHeight = 32;

		wing = new ModelRenderer(this, 0, 0);
		wing.addBox(0F, 0F, 0.5F, 20, 29, 1);
		wing.setTextureSize(32, 32);
		wing.mirror = true;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		matrixStackIn.translate(0, -7F * SCALE, 1F * SCALE * 2);

		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
		matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90));

		boolean isFlying = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isFlying && entity.isAirBorne || entity.fallDistance > 0F;
		float timestep = getAnimationTime(isFlying ? 500 : 6000, entity);
		float angle = (float) Math.sin(timestep) * (isFlying ? 20F : 6F);

		matrixStackIn.translate(0F, -0.5F * SCALE, 0F);

		matrixStackIn.push();
		matrixStackIn.translate(0F, 0F, 2F * SCALE);
		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(30F - angle));
		matrixStackIn.translate(0F, 0F, -1F * SCALE);
		wing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.pop();

		matrixStackIn.push();
		matrixStackIn.translate(0F, 0F, -2F * SCALE);
		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-30F + angle));
		matrixStackIn.translate(0F, 0F, -1F * SCALE);
		wing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.translate(0F, 0F, 1F * SCALE);
		matrixStackIn.pop();
	}
}
