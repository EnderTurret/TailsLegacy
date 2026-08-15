/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiSlot;

import net.enderturret.tailslegacy.forge.client.gui.widget.SimpleGuiList;

@Mixin(GuiSlot.class)
public abstract class MixinGuiSlot {

	@SuppressWarnings("cast")
	@Inject(at = @At("HEAD"), method = "overlayBackground", cancellable = true)
	private void tailslegacy$stopDrawingBackgrounds(CallbackInfo ci) {
		if ((Object) this instanceof SimpleGuiList)
			ci.cancel();
	}
}