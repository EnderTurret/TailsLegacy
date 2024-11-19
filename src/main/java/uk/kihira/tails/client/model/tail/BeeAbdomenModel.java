/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part.SubType;

/**
 * <p>The bee abdomen part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class BeeAbdomenModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, SubType subType, ModelPart model) {
		model.getChild("abdomen").getChild("stinger").visible = "with_stinger".equals(subType.id());
	}
}