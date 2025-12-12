package uk.kihira.tails.forge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import uk.kihira.tails.forge.client.render.layer.TailsArrowLayer;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity {

	@Unique
	private TailsArrowLayer tails$arrowLayer;

	@Inject(at = @At("HEAD"), method = "renderArrowsStuckInEntity", cancellable = true)
	private void tails$renderArrowsStuckInEntity(EntityLivingBase entity, float partialTick, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer)) return;

		if (tails$arrowLayer == null)
			tails$arrowLayer = new TailsArrowLayer((RendererLivingEntity) (Object) this);
		tails$arrowLayer.doRenderLayer(entity, partialTick);

		ci.cancel();
	}
}