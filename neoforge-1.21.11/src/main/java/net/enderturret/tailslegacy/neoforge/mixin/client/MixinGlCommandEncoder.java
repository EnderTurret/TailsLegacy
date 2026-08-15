/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;

@Mixin(GlCommandEncoder.class)
public abstract class MixinGlCommandEncoder {

	// This fixes copyTextureToBuffer() checking the width and height of the entire texture in the buffer size check,
	// when it should be checking the width and height of the specified region instead.
	// (This allows the color pick buffer to store 1 pixel, instead of needing to fit the entire framebuffer.)

	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/textures/GpuTexture;getWidth(I)I", ordinal = 0), method = "copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;IIIII)V")
	private int tailslegacy$fixWidthCheck(int original, GpuTexture texture, GpuBuffer buffer, long offset, Runnable task, int mipLevel, int x, int y, int width, int height) {
		return width;
	}

	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/textures/GpuTexture;getHeight(I)I", ordinal = 0), method = "copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;IIIII)V")
	private int tailslegacy$fixHeightCheck(int original, GpuTexture texture, GpuBuffer buffer, long offset, Runnable task, int mipLevel, int x, int y, int width, int height) {
		return height;
	}
}