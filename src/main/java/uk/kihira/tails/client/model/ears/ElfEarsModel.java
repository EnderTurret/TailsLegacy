/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.model.ModelSerializer;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part.SubType;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The elf ears part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class ElfEarsModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, SubType subType, ModelPart model) {
		model.getChild("feathers").visible = "with_feathers".equals(subType.id());
	}
}