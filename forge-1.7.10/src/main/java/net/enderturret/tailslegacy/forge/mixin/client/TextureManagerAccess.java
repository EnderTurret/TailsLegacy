package net.enderturret.tailslegacy.forge.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.texture.TextureManager;

@Mixin(TextureManager.class)
public interface TextureManagerAccess {

	@Accessor("mapTextureObjects")
	public Map tails$mapTextureObjects();
}