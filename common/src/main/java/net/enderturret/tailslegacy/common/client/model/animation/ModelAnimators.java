/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.animation.impl.AuriaTailPhysicsAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.BirdTailAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.CompositeModelAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.DefaultModelAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.FluffyTailAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.RaccoonTailAnimator;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;

public final class ModelAnimators {

	private static final Map<TResourceLocation, ModelAnimator.Factory> ANIMATORS = new HashMap<>();

	static {
		registerBuiltin("none", (a, b) -> null);
		registerBuiltin("default", DefaultModelAnimator::parse);
		registerBuiltin("composite", CompositeModelAnimator::parse);

		if (TailsInternal.UNLOCK_EXPERIMENTAL_ANIMATORS || TailsPlatform.get().isDevEnvironment())
			registerBuiltin("auria_tail_physics", AuriaTailPhysicsAnimator::new);

		registerBuiltin("bird_tail", BirdTailAnimator::new);
		registerBuiltin("fluffy_tail", FluffyTailAnimator::new);
		registerBuiltin("raccoon_tail", RaccoonTailAnimator::new);
	}

	public static void register(TResourceLocation id, ModelAnimator.Factory factory) {
		ANIMATORS.put(Objects.requireNonNull(id, "id"), Objects.requireNonNull(factory, "factory"));
	}

	private static void registerBuiltin(String id, ModelAnimator.Factory factory) {
		register(TailsPlatform.get().newResourceLocation(id), factory);
	}

	public static @Nullable ModelAnimator fromJson(TailsModelPart model, JsonObject obj) {
		final String type = TailsGsonHelper.getAsString(obj, "type");
		final TResourceLocation id = TailsPlatform.get().parseResourceLocation(type);

		final ModelAnimator.Factory factory = ANIMATORS.get(id);
		return factory == null ? null :  factory.parse(model, obj);
	}
}