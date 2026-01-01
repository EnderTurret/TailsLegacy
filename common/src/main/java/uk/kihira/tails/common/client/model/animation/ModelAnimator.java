/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.animation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.part.Part;

public interface ModelAnimator {

	public default boolean isTicking() { return false; }
	public default AnimatorStorage tick(@Nullable AnimatorStorage storage, TailsEntity entity) { return storage; }

	public void setupAnim(@Nullable AnimatorStorage storage, TailsEntity entity, TailsModelPart model, Part.SubType subType, float partialTick);

	public static interface Factory {
		public ModelAnimator parse(TailsModelPart model, JsonObject obj);
	}
}