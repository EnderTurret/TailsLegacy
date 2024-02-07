/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.api;

import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.part.PartRegistry;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>An event fired when {@linkplain PartRenderer PartRenderers} are being registered.
 * Use this event to link part renderers to {@linkplain Part Parts}.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @author EnderTurret
 * @see PartRenderer
 * @see PartRegistry
 * @see PartRenderRegistry
 */
public class RegisterPartRenderersEvent extends Event implements IModBusEvent {

	private final Map<ResourceLocation, PartRenderer> entries;

	@Internal
	public RegisterPartRenderersEvent(Map<ResourceLocation, PartRenderer> map) {
		entries = map;
	}

	/**
	 * Registers a {@link PartRenderer} for the part identified by the given id.
	 * @param part The id of the part to link the renderer to.
	 * @param renderer The part renderer.
	 * @see #register(PartRegistry.PartReference, PartRenderer)
	 */
	public void register(ResourceLocation part, PartRenderer renderer) {
		Objects.requireNonNull(part);
		Objects.requireNonNull(renderer);
		entries.put(part, renderer);
	}

	/**
	 * Registers a {@link PartRenderer} for the part referenced by the given part reference.
	 * @param reference A reference to the part to link the renderer to.
	 * @param renderer The part renderer.
	 * @see #register(ResourceLocation, PartRenderer)
	 * @see PartRegistry#reference(ResourceLocation)
	 */
	public void register(PartRegistry.PartReference reference, PartRenderer renderer) {
		register(reference.id(), renderer);
	}

	/**
	 * {@link PartModel} version of {@link #register(uk.kihira.tails.client.part.PartRegistry.PartReference, PartRenderer) register(PartReference, PartRenderer)}.
	 * @param reference A reference to the part to link the renderer to.
	 * @param model The part model.
	 * @see #register(ResourceLocation, PartRenderer)
	 * @see #register(uk.kihira.tails.client.part.PartRegistry.PartReference, PartRenderer)
	 * @see PartRegistry#reference(ResourceLocation)
	 */
	public void register(PartRegistry.PartReference reference, PartModel model) {
		register(reference, new PartRenderer(model));
	}
}