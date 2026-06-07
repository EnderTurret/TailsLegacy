/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model;

import java.util.Collections;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.render.RenderContext;

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
						Collections.singletonList(new TailsCubeDefinition(xOffset, yOffset, zOffset, xSize, ySize, zSize, false, xTex, yTex))
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
			case "very_short": ctx.poseStack().t$translate(0, 0, 4 / 16D); break;
			case "short": ctx.poseStack().t$translate(0, 0, 3 / 16D); break;
			case "default": ctx.poseStack().t$translate(0, 0, 2 / 16D); break;
			case "long": ctx.poseStack().t$translate(0, 0, 1 / 16D); break;
			case "very_long": break;
		}

		ctx.render(root);

		ctx.poseStack().t$pop();
	}
}
