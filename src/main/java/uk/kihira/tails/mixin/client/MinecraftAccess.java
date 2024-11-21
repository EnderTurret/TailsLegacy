package uk.kihira.tails.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public interface MinecraftAccess {

	@Accessor("authenticationService")
	public YggdrasilAuthenticationService tails$authenticationService();
}