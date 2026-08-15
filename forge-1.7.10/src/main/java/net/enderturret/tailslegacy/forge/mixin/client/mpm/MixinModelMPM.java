/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client.mpm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.enderturret.tailslegacy.forge.client.ClientEventHandler;
import net.enderturret.tailslegacy.forge.client.render.layer.PartLayer;

@Pseudo
@Mixin(targets = "noppes/mpm/client/model/ModelMPM", remap = false)
public abstract class MixinModelMPM {

	@Inject(
			at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V"),
			method = "renderHead")
	private void tailslegacy$renderHeadParts(Entity entity, float scale, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer)) return;

		final RenderPlayer renderer = ClientEventHandler.ACTIVE_PLAYER_RENDERER.get();
		if (renderer == null || (ModelBiped) (Object) this != renderer.modelBipedMain) return;

		PartLayer.doRenderLayer((EntityLivingBase) entity, ClientEventHandler.partialTick, "head", ((ModelBiped) (Object) this).bipedHead);
	}

	@Inject(
			at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V"),
			method = "renderBody")
	private void tailslegacy$renderBodyParts(Entity entity, float scale, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer)) return;

		final RenderPlayer renderer = ClientEventHandler.ACTIVE_PLAYER_RENDERER.get();
		if (renderer == null || (ModelBiped) (Object) this != renderer.modelBipedMain) return;

		PartLayer.doRenderLayer((EntityLivingBase) entity, ClientEventHandler.partialTick, "body", ((ModelBiped) (Object) this).bipedBody);
	}
}