/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for bunny tails.
 */
public class BunnyTailModel extends PartModel {

	private final ModelPart tailBase;

	public BunnyTailModel() {
		tailBase = new ModelPart(this);

		tailBase.addBox(0.0F, 0.0F, 0.0F, 4, 3, 3, 0.0F);
		tailBase.setPos(-2.0F, -1.5F, 0.0F);
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		final float timestep = getAnimationTime(4000F, entity);

		this.setRotationAngles(0, timestep, 1F, 1F, 0, 0, partialTicks, entity);

		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	private void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
		setRotationDegrees(tailBase, xAngle, yAngle, 0F);
	}
}