/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.model.animation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.animation.AnimatorStorage;
import net.enderturret.tailslegacy.common.client.model.animation.ModelAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.ModelAnimators;
import net.enderturret.tailslegacy.common.client.part.SubType;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;

public final class CompositeModelAnimator implements ModelAnimator {

	private final ModelAnimator[] animators;

	public CompositeModelAnimator(ModelAnimator... animators) {
		this.animators = animators;
	}

	public CompositeModelAnimator(Collection<ModelAnimator> animators) {
		this(animators.toArray(new ModelAnimator[0]));
	}

	public static ModelAnimator parse(TailsModelPart model, JsonObject obj) {
		final List<ModelAnimator> list = new ArrayList<>();

		int idx = 0;
		for (JsonElement animatorJson : TailsGsonHelper.getAsJsonArray(obj, "animators")) {
			final ModelAnimator animator = ModelAnimators.fromJson(model, TailsGsonHelper.convertToJsonObject(animatorJson, "animators[" + idx + "]"));
			if (animator != null)
				list.add(animator);
			idx++;
		}

		return new CompositeModelAnimator(list);
	}

	@Override
	public boolean isTicking() {
		for (ModelAnimator anim : animators)
			if (anim.isTicking())
				return true;

		return false;
	}

	@Override
	public AnimatorStorage tick(@Nullable AnimatorStorage storage, TailsEntity entity) {
		if (!(storage instanceof Storage)) storage = new Storage(animators.length);
		final Storage store = (Storage) storage;

		for (int i = 0; i < animators.length; i++)
			store.storages[i] = animators[i].tick(store.storages[i], entity);

		return storage;
	}

	@Override
	public void setupAnim(@Nullable AnimatorStorage storage, TailsEntity entity, TailsModelPart model, SubType subType, float partialTick) {
		if (!(storage instanceof Storage)) {
			for (int i = 0; i < animators.length; i++)
				animators[i].setupAnim(null, entity, model, subType, partialTick);
			return;
		}

		final Storage store = (Storage) storage;

		for (int i = 0; i < animators.length; i++)
			animators[i].setupAnim(store.storages[i], entity, model, subType, partialTick);
	}

	public static final class Storage implements AnimatorStorage {

		public final AnimatorStorage[] storages;

		public Storage(int length) {
			storages = new AnimatorStorage[length];
		}

		@Override
		public AnimatorStorage copy() {
			final Storage ret = new Storage(storages.length);

			for (int i = 0; i < storages.length; i++)
				ret.storages[i] = storages[i].copy();

			return ret;
		}
	}
}