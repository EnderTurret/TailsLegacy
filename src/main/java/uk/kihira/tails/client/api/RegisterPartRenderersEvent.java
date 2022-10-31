/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.api;

import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import uk.kihira.tails.client.part.PartRegistry;
import uk.kihira.tails.client.render.part.PartRenderer;

public class RegisterPartRenderersEvent extends Event implements IModBusEvent {

	private final Map<ResourceLocation, PartRenderer> entries;

	@Internal
	public RegisterPartRenderersEvent(Map<ResourceLocation, PartRenderer> map) {
		entries = map;
	}

	public void register(ResourceLocation part, PartRenderer renderer) {
		Objects.requireNonNull(part);
		Objects.requireNonNull(renderer);
		entries.put(part, renderer);
	}

	public void register(PartRegistry.PartReference reference, PartRenderer renderer) {
		register(reference.id(), renderer);
	}
}