package uk.kihira.tails.common.client.model.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.animation.impl.BirdTailAnimator;
import uk.kihira.tails.common.client.model.animation.impl.DefaultModelAnimator;
import uk.kihira.tails.common.client.model.animation.impl.FluffyTailAnimator;
import uk.kihira.tails.common.client.model.animation.impl.RaccoonTailAnimator;
import uk.kihira.tails.common.gson.TailsGsonHelper;

public final class ModelAnimators {

	private static final Map<TResourceLocation, ModelAnimator.Factory> ANIMATORS = new HashMap<>();

	static {
		registerBuiltin("none", (a, b) -> null);
		registerBuiltin("default", DefaultModelAnimator::parse);
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