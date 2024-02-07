/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
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

/**
 * <p>Defines a "unique" configuration of a part.</p>
 * <p>Each configuration is a separate instance of the part.
 * For example, the nine tail variant of the fluffy tail has nine configurations -- one for each tail.</p>
 * @author EnderTurret
 */
public class PartConfiguration {

	private final List<ModelPart> parts;
	private Map<ModelPart, ModelPart[]> parents = Map.of();
	private final Translator translator;

	public PartConfiguration(List<ModelPart> parts, Translator translator) {
		this.parts = parts;
		this.translator = translator;
	}

	public PartConfiguration(List<ModelPart> parts) {
		this(parts, Translator.EMPTY);
	}

	public PartConfiguration setParents(ModelPart child, ModelPart... hierarchy) {
		if (parents.isEmpty())
			parents = new HashMap<>();

		parents.put(child, hierarchy);

		return this;
	}

	private ModelPart[] visible;

	/**
	 * Recomputes the visibilities of each cube in the configuration.
	 */
	public void prime() {
		visible = parts.stream().filter(p -> p.visible).toArray(ModelPart[]::new);
	}

	/**
	 * @return The visible parts.
	 * @see #prime()
	 */
	public ModelPart[] visible() {
		return visible;
	}

	/**
	 * Returns a random visible part from the configuration.
	 * @param rand The random to use for deciding which part to return.
	 * @return The part.
	 */
	public ModelPart randomPart(RandomSource rand) {
		return visible[rand.nextInt(visible.length)];
	}

	/**
	 * Performs any necessary transformations to match the location of the given part.
	 * @param info The part info.
	 * @param poseStack The {@link PoseStack} to use for transformations.
	 * @param partialTick The partial tick.
	 * @param entity The entity being rendered.
	 * @param part The part in question.
	 */
	public void translate(ClientPartInfo info, PoseStack poseStack, float partialTick, LivingEntity entity, ModelPart part) {
		translator.translate(info, poseStack, partialTick, entity);

		for (ModelPart part2 : parents.getOrDefault(part, visible())) {
			if (part2 == part) break;
			part2.translateAndRotate(poseStack);
		}

		part.translateAndRotate(poseStack);
	}

	/**
	 * Represents a part configuration for a whole player.
	 * @author EnderTurret
	 */
	public static class Player extends PartConfiguration {

		private final PlayerModel<?> model;

		/**
		 * @param model The model of the player.
		 */
		public Player(PlayerModel<?> model) {
			super(List.of());
			this.model = model;
		}

		@Override
		public ModelPart randomPart(RandomSource rand) {
			return model.getRandomModelPart(rand);
		}
	}

	/**
	 * A callback for any extra necessary translation.
	 * @author EnderTurret
	 */
	public static interface Translator {

		/**
		 * A no-op {@link Translator}.
		 */
		public static final Translator EMPTY = (info, poseStack, partialTick, entity) -> {};

		/**
		 * Performs various transformations using the provided {@link PoseStack}.
		 * @param info The part info.
		 * @param poseStack The {@link PoseStack} to use for transformations.
		 * @param partialTick The partial tick.
		 * @param entity The entity being rendered.
		 */
		public void translate(ClientPartInfo info, PoseStack poseStack, float partialTick, LivingEntity entity);
	}
}