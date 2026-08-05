package net.enderturret.tailslegacy.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

import net.enderturret.tailslegacy.fabric.client.render.RenderStates;

@Mixin(AvatarRenderer.class)
public abstract class MixinAvatarRenderer {

	@Inject(at = @At("RETURN"), method = "extractRenderState")
	private void tails$extractRenderState(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		RenderStates.addTailsRenderData(entity, state, partialTicks);
	}
}