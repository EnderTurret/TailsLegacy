/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.api;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.model.PartModel;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.PartRegistry;
import uk.kihira.tails.common.client.render.PartRenderRegistry;
import uk.kihira.tails.common.client.render.part.PartRenderer;

/**
 * <p>An event fired when {@linkplain PartRenderer PartRenderers} are being registered.
 * Use this event to link part renderers to {@linkplain Part Parts}.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not have a result.</p>
 *
 * <p>This event is fired on the {@linkplain Side#CLIENT logical client}.</p>
 *
 * @author EnderTurret
 * @see PartRenderer
 * @see PartRegistry
 * @see PartRenderRegistry
 */
public class RegisterPartRenderersEvent extends Event {

	private final PartRendererRegistrar registrar;

	@Internal
	public RegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		this.registrar = registrar;
	}

	/**
	 * Registers a {@link PartRenderer} for the part identified by the given id.
	 * @param part The id of the part to link the renderer to.
	 * @param renderer The part renderer.
	 * @see #register(PartRegistry.PartReference, PartRenderer)
	 */
	public void register(TResourceLocation part, PartRenderer renderer) {
		registrar.register(part, renderer);
	}

	/**
	 * Registers a {@link PartRenderer} for the part referenced by the given part reference.
	 * @param reference A reference to the part to link the renderer to.
	 * @param renderer The part renderer.
	 * @see #register(TResourceLocation, PartRenderer)
	 * @see PartRegistry#reference(TResourceLocation)
	 */
	public void register(PartRegistry.PartReference reference, PartRenderer renderer) {
		registrar.register(reference, renderer);
	}

	/**
	 * {@link PartModel} version of {@link #register(uk.kihira.tails.common.client.part.PartRegistry.PartReference, PartRenderer) register(PartReference, PartRenderer)}.
	 * @param reference A reference to the part to link the renderer to.
	 * @param model The part model.
	 * @see #register(TResourceLocation, PartRenderer)
	 * @see #register(uk.kihira.tails.common.client.part.PartRegistry.PartReference, PartRenderer)
	 * @see PartRegistry#reference(TResourceLocation)
	 */
	public void register(PartRegistry.PartReference reference, PartModel model) {
		registrar.register(reference, model);
	}
}