/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.part.ClientPartInfo;

public class PartConfiguration {

	private final ModelPart root;
	private final List<ModelPart> parts;
	private final Map<ModelPart, ModelPart[]> parents = new HashMap<>();
	private final Translator translator;

	public PartConfiguration(ModelPart root, List<ModelPart> parts, Translator translator) {
		this.root = root;
		this.parts = parts;
		this.translator = translator;
	}

	public PartConfiguration(ModelPart root, List<ModelPart> parts) {
		this(root, parts, Translator.EMPTY);
	}

	public PartConfiguration setParents(ModelPart child, ModelPart... hierarchy) {
		parents.put(child, hierarchy);
		return this;
	}

	private ModelPart[] visible;

	public void prime() {
		visible = parts.stream().filter(p -> p.visible).toArray(ModelPart[]::new);
	}

	public ModelPart root() {
		return root;
	}

	public ModelPart[] visible() {
		return visible;
	}

	public ModelPart randomPart(RandomSource rand) {
		return visible[rand.nextInt(visible.length)];
	}

	public void translate(ClientPartInfo info, PoseStack poseStack, float partialTick, LivingEntity entity, ModelPart part) {
		translator.translate(info, poseStack, partialTick, entity);

		for (ModelPart part2 : parents.getOrDefault(part, visible())) {
			if (part2 == part) break;
			part2.translateAndRotate(poseStack);
		}

		part.translateAndRotate(poseStack);
	}

	public static class Player extends PartConfiguration {

		private final PlayerModel<?> model;

		public Player(PlayerModel<?> model) {
			super(null, List.of());
			this.model = model;
		}

		@Override
		public ModelPart randomPart(RandomSource rand) {
			return model.getRandomModelPart(rand);
		}
	}

	public static interface Translator {

		public static final Translator EMPTY = (info, poseStack, partialTick, entity) -> {};

		public void translate(ClientPartInfo info, PoseStack poseStack, float partialTick, LivingEntity entity);
	}
}