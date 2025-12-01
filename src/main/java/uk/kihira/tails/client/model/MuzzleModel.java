/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.common2.client.TailsClientPlatform;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.TailsCubeDefinition;
import uk.kihira.tails.common2.client.model.TailsPartDefinition;

/**
 * The muzzle part model.
 */
@Internal
public final class MuzzleModel extends PartModel {

	private final TailsModelPart root;

	@Internal
	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
		root = TailsClientPlatform.get().bake(
				new TailsPartDefinition(
						List.of(new TailsCubeDefinition(xOffset, yOffset, zOffset, xSize, ySize, zSize, false, xTex, yTex))
						),
				32, 32);
	}

	@Internal
	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
		this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.poseStack().t$push();

		ctx.poseStack().t$translate(0, -0.001D, 0);

		switch (ctx.info().getSubType().id()) {
			case "very_short" -> ctx.poseStack().t$translate(0, 0, 4 / 16D);
			case "short" -> ctx.poseStack().t$translate(0, 0, 3 / 16D);
			case "standard" -> ctx.poseStack().t$translate(0, 0, 2 / 16D);
			case "long" -> ctx.poseStack().t$translate(0, 0, 1 / 16D);
			case "very_long" -> {}
		}

		ctx.render(root);

		ctx.poseStack().t$pop();
	}
}
