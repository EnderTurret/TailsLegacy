package uk.kihira.tails.forge.mixin.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import uk.kihira.tails.forge.client.ClientEventHandler;
import uk.kihira.tails.forge.client.render.layer.PartLayer;

@Mixin(ModelBiped.class)
public abstract class MixinModelBiped {

	@Inject(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", shift = At.Shift.AFTER),
			method = "render",
			slice = @Slice(
					from = @At(value = "FIELD", ordinal = 1, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/model/ModelBiped;bipedHead:Lnet/minecraft/client/model/ModelRenderer;"),
					to = @At(value = "FIELD", ordinal = 1, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/model/ModelBiped;bipedBody:Lnet/minecraft/client/model/ModelRenderer;")
					))
	private void tails$renderHeadParts(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer)) return;

		final RenderPlayer renderer = ClientEventHandler.ACTIVE_PLAYER_RENDERER.get();
		if (renderer == null) return;

		PartLayer.doRenderLayer((EntityLivingBase) entity, ageInTicks - entity.ticksExisted, "head", ((ModelBiped) (Object) this).bipedHead);
	}

	@Inject(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", shift = At.Shift.AFTER),
			method = "render",
			slice = @Slice(
					from = @At(value = "FIELD", ordinal = 1, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/model/ModelBiped;bipedBody:Lnet/minecraft/client/model/ModelRenderer;"),
					to = @At(value = "FIELD", ordinal = 1, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/model/ModelBiped;bipedRightArm:Lnet/minecraft/client/model/ModelRenderer;")
					))
	private void tails$renderBodyParts(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer)) return;

		final RenderPlayer renderer = ClientEventHandler.ACTIVE_PLAYER_RENDERER.get();
		if (renderer == null) return;

		PartLayer.doRenderLayer((EntityLivingBase) entity, ageInTicks - entity.ticksExisted, "body", ((ModelBiped) (Object) this).bipedBody);
	}
}