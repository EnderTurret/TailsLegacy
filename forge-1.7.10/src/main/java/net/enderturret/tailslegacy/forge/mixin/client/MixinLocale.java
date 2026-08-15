/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.Locale;

import net.enderturret.tailslegacy.forge.client.ClientEventHandler;

@Mixin(Locale.class)
public abstract class MixinLocale {

	@Shadow
	private Map<String, String> field_135032_a;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/Locale;loadLocaleData(Ljava/io/InputStream;)V"), method = "loadLocaleData(Ljava/util/List;)V")
	private void tails$loadModernLangFiles(List resourcesList, CallbackInfo ci, @Local(ordinal = 0, index = 2) IResource resource) {
		if (!(resource instanceof SimpleResourceAccess)) return;
		ClientEventHandler.handleLoadingLangFile((SimpleResourceAccess) resource, field_135032_a);
	}
}