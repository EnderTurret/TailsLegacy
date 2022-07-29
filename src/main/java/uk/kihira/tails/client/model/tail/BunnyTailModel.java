/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for bunny tails.
 */
public class BunnyTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tail;

	public BunnyTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("tail", CubeListBuilder.create()
				.addBox(0, 0, 0, 4, 3, 3), PartPose.offset(-2, -1.5F, 0));
		root = rootDef.bake(64, 32);
		tail = root.getChild("tail");

		config = new PartConfiguration(tail, List.of(tail));
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, int subtype, float partialTick) {
		tail.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}