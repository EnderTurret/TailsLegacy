/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import static uk.kihira.tails.client.part.PartRegistry.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.model.MuzzleModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.client.render.part.WingRenderer;
import uk.kihira.tails.common.Tails;

/**
 * Manages the registry of {@link PartRenderer PartRenderers} that correspond to different parts.
 * @author EnderTurret
 */
@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PartRenderRegistry {

	private static final Map<ResourceLocation, PartRenderer> PART_RENDERER_REGISTRY = new HashMap<>();

	@SubscribeEvent
	static void registerPartRenderers(RegisterPartRenderersEvent e) {
		e.register(BIG_WINGS, new WingRenderer());

		e.register(STANDARD_MUZZLE, new MuzzleModel(-2f, -3f, -9f, 4, 3, 5));
		e.register(SLIM_MUZZLE, new MuzzleModel(-2f, -2f, -9f, 4, 2, 5));
		e.register(THIN_MUZZLE, new MuzzleModel(-1.5f, -2f, -9f, 3, 2, 5, 0, 9));
	}

	/**
	 * Reloads the entire part renderer registry.
	 * An event will be fired to rebuild the part renderer registry.
	 */
	@Internal
	public static void reload() {
		PART_RENDERER_REGISTRY.clear();

		final Map<ResourceLocation, PartRenderer> map = new ConcurrentHashMap<>();
		ModLoader.get().postEvent(new RegisterPartRenderersEvent(map));

		PART_RENDERER_REGISTRY.putAll(map);
	}

	/**
	 * Returns the {@link PartRenderer} associated with the given part, or {@code null} if no such renderer exists.
	 * @param part The {@link Part} to retrieve the {@link PartRenderer} for.
	 * @return The {@link PartRenderer}.
	 */
	@Nullable
	public static PartRenderer getRenderer(Part part) {
		if (part == null) throw new NullPointerException();
		return PART_RENDERER_REGISTRY.get(part.getId());
	}
}