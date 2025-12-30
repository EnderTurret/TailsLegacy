/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsRandomSource;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ModelPredicate;

/**
 * <p>Defines a "unique" configuration of a part.</p>
 * <p>Each configuration is a separate instance of the part.
 * For example, the nine tail variant of the fluffy tail has nine configurations -- one for each tail.</p>
 * @author EnderTurret
 */
public class PartConfiguration {

	private final List<TailsModelPart> parts;
	private Map<TailsModelPart, TailsModelPart[]> parents = Collections.emptyMap();
	private Translator translator;

	private PartConfiguration(List<TailsModelPart> parts, Map<TailsModelPart, TailsModelPart[]> parents, Translator translator) {
		this.parts = parts;
		this.parents = parents;
		this.translator = translator;
	}

	public PartConfiguration(List<TailsModelPart> parts, Translator translator) {
		this.parts = parts;
		this.translator = translator;

		if (parts.isEmpty() && getClass() == PartConfiguration.class)
			throw new IllegalArgumentException("PartConfiguration contains no parts");

		for (TailsModelPart part : parts)
			if (part.t$isEmpty()) // Also serves as a null check.
				throw new IllegalArgumentException("Part " + part + " has no cubes!");
	}

	public PartConfiguration(List<TailsModelPart> parts) {
		this(parts, Translator.EMPTY);
	}

	public static PartConfiguration derive(TailsModelPart root, ModelPredicate predicate) {
		final List<TailsModelPart> queue = new ArrayList<>();
		final Map<TailsModelPart, TailsModelPart> parentsByChildren = new IdentityHashMap<>();
		queue.add(root);

		final List<TailsModelPart> partsWithCubes = new ArrayList<>();

		while (!queue.isEmpty()) {
			final TailsModelPart part = queue.remove(0);

			if (!part.t$isEmpty() && predicate.test(root, part))
				partsWithCubes.add(part);

			for (TailsModelPart child : part.t$getChildren().values()) {
				parentsByChildren.put(child, part);
				queue.add(child);
			}
		}

		final Map<TailsModelPart, TailsModelPart[]> allParents = new IdentityHashMap<>();

		for (TailsModelPart part : partsWithCubes) {
			final List<TailsModelPart> parents = new ArrayList<>();

			TailsModelPart parent = part;
			while ((parent = parentsByChildren.get(parent)) != null) {
				if (parent.t$hasInitialPose())
					parents.add(0, parent); // We're traversing upwards, so insert the parents in reverse order.
			}

			allParents.put(part, parents.toArray(new TailsModelPart[0]));
		}

		return new PartConfiguration(
				Collections.unmodifiableList(new ArrayList<>(partsWithCubes)),
				Collections.unmodifiableMap(new IdentityHashMap<>(allParents)),
				Translator.EMPTY);
	}

	/**
	 * <p>
	 * Defines the parent {@code ModelParts} of a given {@code ModelPart}.
	 * </p>
	 * <p>
	 * This is required to set up the correct pose for each {@code ModelPart}.
	 * However, this is only necessary if the parent parts change the pose state.
	 * </p>
	 * @param child The {@code ModelPart} the hierarchy is for.
	 * @param hierarchy The {@code ModelPart} hierarchy.
	 * @return {@code this}.
	 */
	public PartConfiguration setParents(TailsModelPart child, TailsModelPart... hierarchy) {
		if (parents.isEmpty())
			parents = new HashMap<>();

		parents.put(child, hierarchy);

		return this;
	}

	public PartConfiguration withTranslator(Translator translator) {
		this.translator = translator;
		return this;
	}

	public PartConfiguration copy() {
		return new PartConfiguration(parts, parents, translator);
	}

	private TailsModelPart[] visible;

	/**
	 * Recomputes the visibilities of each cube in the configuration.
	 */
	public void prime() {
		visible = parts.stream().filter(p -> p.t$isVisible()).toArray(TailsModelPart[]::new);
	}

	/**
	 * @return The visible parts.
	 * @see #prime()
	 */
	public TailsModelPart[] visible() {
		return visible;
	}

	/**
	 * Returns a random visible part from the configuration.
	 * @param rand The random to use for deciding which part to return.
	 * @return The part.
	 */
	public TailsModelPart randomPart(TailsRandomSource rand) {
		return visible[rand.t$nextInt(visible.length)];
	}

	/**
	 * Performs any necessary transformations to match the location of the given part.
	 * @param info The part info.
	 * @param poseStack The {@link TailsPoseStack} to use for transformations.
	 * @param partialTick The partial tick.
	 * @param entity The entity being rendered.
	 * @param part The part in question.
	 */
	public void translate(ClientPartInfo info, TailsPoseStack poseStack, float partialTick, TailsEntity entity, TailsModelPart part) {
		translator.translate(info, poseStack, partialTick, entity);

		for (TailsModelPart part2 : parents.getOrDefault(part, new TailsModelPart[0])) {
			if (part2 == part) break;
			part2.t$translateAndRotate(poseStack);
		}

		part.t$translateAndRotate(poseStack);
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
		 * Performs various transformations using the provided {@link TailsPoseStack}.
		 * @param info The part info.
		 * @param poseStack The {@link TailsPoseStack} to use for transformations.
		 * @param partialTick The partial tick.
		 * @param entity The entity being rendered.
		 */
		public void translate(ClientPartInfo info, TailsPoseStack poseStack, float partialTick, TailsEntity entity);
	}
}