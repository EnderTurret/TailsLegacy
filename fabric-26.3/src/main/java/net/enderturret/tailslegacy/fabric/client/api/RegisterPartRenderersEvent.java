/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.api;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.enderturret.tailslegacy.common.client.api.PartRendererRegistrar;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.part.PartRegistry;
import net.enderturret.tailslegacy.common.client.render.PartRenderRegistry;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;

/**
 * <p>An event fired when {@linkplain PartRenderer PartRenderers} are being registered.
 * Use this event to link part renderers to {@linkplain Part Parts}.</p>
 *
 * @author EnderTurret
 * @see PartRenderer
 * @see PartRegistry
 * @see PartRenderRegistry
 */
public class RegisterPartRenderersEvent {

	public static final Event<Handler> REGISTER_PART_RENDERERS = EventFactory.createArrayBacked(Handler.class, array -> registrar -> {
		for (Handler handler : array)
			handler.register(registrar);
	});

	private final PartRendererRegistrar registrar;

	@Internal
	public RegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		this.registrar = registrar;
	}

	@FunctionalInterface
	public static interface Handler {
		public void register(PartRendererRegistrar registrar);
	}
}