package uk.kihira.tails.neoforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;

@Mixin(StuckInBodyLayer.class)
public interface StuckInBodyLayerAccess {

	@Invoker("submitStuckItem")
	public void tails$submitStuckItem(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, float x, float y, float z, int outlineColor);
}