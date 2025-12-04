package uk.kihira.tails.neoforge.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

@Mixin(TextureManager.class)
public interface TextureManagerAccess {

	@Accessor("byPath")
	public Map<ResourceLocation, AbstractTexture> tails$byPath();
}