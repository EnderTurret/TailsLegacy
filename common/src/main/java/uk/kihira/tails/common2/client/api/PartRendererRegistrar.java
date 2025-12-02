package uk.kihira.tails.common2.client.api;

import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.model.PartModel;
import uk.kihira.tails.common2.client.part.PartRegistry;
import uk.kihira.tails.common2.client.render.part.PartRenderer;

public final class PartRendererRegistrar {

	private final Map<TResourceLocation, PartRenderer> entries;

	@Internal
	public PartRendererRegistrar(Map<TResourceLocation, PartRenderer> map) {
		entries = map;
	}

	/**
	 * Registers a {@link PartRenderer} for the part identified by the given id.
	 * @param part The id of the part to link the renderer to.
	 * @param renderer The part renderer.
	 * @see #register(PartRegistry.PartReference, PartRenderer)
	 */
	public void register(TResourceLocation part, PartRenderer renderer) {
		Objects.requireNonNull(part);
		Objects.requireNonNull(renderer);
		entries.put(part, renderer);
	}

	/**
	 * Registers a {@link PartRenderer} for the part referenced by the given part reference.
	 * @param reference A reference to the part to link the renderer to.
	 * @param renderer The part renderer.
	 * @see #register(TResourceLocation, PartRenderer)
	 * @see PartRegistry#reference(TResourceLocation)
	 */
	public void register(PartRegistry.PartReference reference, PartRenderer renderer) {
		register(reference.id(), renderer);
	}

	/**
	 * {@link PartModel} version of {@link #register(uk.kihira.tails.common2.client.part.PartRegistry.PartReference, PartRenderer) register(PartReference, PartRenderer)}.
	 * @param reference A reference to the part to link the renderer to.
	 * @param model The part model.
	 * @see #register(TResourceLocation, PartRenderer)
	 * @see #register(uk.kihira.tails.common2.client.part.PartRegistry.PartReference, PartRenderer)
	 * @see PartRegistry#reference(TResourceLocation)
	 */
	public void register(PartRegistry.PartReference reference, PartModel model) {
		register(reference, new PartRenderer(model));
	}
}