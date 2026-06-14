/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.ResourceLocation;

import net.enderturret.tailslegacy.common.TailsPlatform;

import cpw.mods.fml.client.FMLClientHandler;

@Mixin(Locale.class)
public abstract class MixinLocale {

	@Shadow
	private Map<String, String> field_135032_a;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/Locale;loadLocaleData(Ljava/io/InputStream;)V"), method = "loadLocaleData(Ljava/util/List;)V")
	private void tails$loadModernLangFiles(List resourcesList, CallbackInfo ci, @Local(ordinal = 0, index = 2) IResource resource) {
		if (!(resource instanceof SimpleResourceAccess)) return;

		final ResourceLocation rl = ((SimpleResourceAccess) resource).tails$srResourceLocation();
		if (!TailsPlatform.MOD_ID.equals(rl.getResourceDomain())) return;

		final String modernFile = rl.getResourcePath()
				.toLowerCase(java.util.Locale.ENGLISH) // Before 1.11(?), lang files were en_US.lang etc.
				.replace(".lang", ".json");

		final ResourceLocation loc = new ResourceLocation(TailsPlatform.MOD_ID, modernFile);
		final IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(TailsPlatform.MOD_ID);

		try (InputStream is = pack.getInputStream(loc); InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr)) {
			final JsonElement elem = new JsonParser().parse(br);
			tails$loadModernLangFile(elem);
		} catch (Exception e) {
			TailsPlatform.get().logError("Exception reading resource {}:", loc, e);
		}
	}

	@Unique
	private void tails$loadModernLangFile(JsonElement elem) {
		final JsonObject obj = elem.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : obj.entrySet())
			field_135032_a.put(entry.getKey(), entry.getValue().getAsString());
	}
}