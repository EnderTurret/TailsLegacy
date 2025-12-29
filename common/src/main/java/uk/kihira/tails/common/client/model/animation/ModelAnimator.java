/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.animation;

import com.google.gson.JsonObject;

import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.Part.SubType;

public interface ModelAnimator {

	public void setupAnim(TailsEntity entity, TailsModelPart model, Part.SubType subType, float partialTick);

	public static interface Factory {
		public ModelAnimator parse(TailsModelPart model, JsonObject obj);
	}
}