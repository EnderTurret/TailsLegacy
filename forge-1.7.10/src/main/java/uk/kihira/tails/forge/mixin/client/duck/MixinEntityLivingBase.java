/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.client.duck;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import uk.kihira.tails.common.client.duck.TailsEntity;

@Mixin(EntityLivingBase.class)
@SuppressWarnings("cast")
public class MixinEntityLivingBase implements TailsEntity {

	@Override
	public double t$x() {
		return ((EntityLivingBase) (Object) this).posX;
	}

	@Override
	public double t$y() {
		return ((EntityLivingBase) (Object) this).posY;
	}

	@Override
	public double t$z() {
		return ((EntityLivingBase) (Object) this).posZ;
	}

	@Override
	public double t$xO() {
		return ((EntityLivingBase) (Object) this).prevPosX;
	}

	@Override
	public double t$yO() {
		return ((EntityLivingBase) (Object) this).prevPosY;
	}

	@Override
	public double t$zO() {
		return ((EntityLivingBase) (Object) this).prevPosZ;
	}

	@Override
	public float t$bob() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).cameraYaw : 0;
	}

	@Override
	public float t$bobO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).prevCameraYaw : 0;
	}

	@Override
	public float t$walkDistance() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).distanceWalkedModified : 0;
	}

	@Override
	public float t$walkDistanceO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).prevDistanceWalkedModified : 0;
	}

	@Override
	public double t$xCloak() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).chasingPosX : 0;
	}

	@Override
	public double t$yCloak() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).chasingPosY : 0;
	}

	@Override
	public double t$zCloak() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).chasingPosZ : 0;
	}

	@Override
	public double t$xCloakO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).prevChasingPosX : 0;
	}

	@Override
	public double t$yCloakO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).prevChasingPosY : 0;
	}

	@Override
	public double t$zCloakO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).prevChasingPosZ : 0;
	}

	@Override
	public float t$yBodyRot() {
		return ((EntityLivingBase) (Object) this).renderYawOffset;
	}

	@Override
	public float t$yBodyRotO() {
		return ((EntityLivingBase) (Object) this).prevRenderYawOffset;
	}

	@Override
	public float t$limbSwing() {
		return ((EntityLivingBase) (Object) this).limbSwing;
	}

	@Override
	public float t$limbSwingAmount() {
		return ((EntityLivingBase) (Object) this).limbSwingAmount;
	}

	@Override
	public boolean t$isPassenger() {
		return ((EntityLivingBase) (Object) this).getRidingEntity() != null;
	}

	@Override
	public boolean t$isCrouching() {
		return ((EntityLivingBase) (Object) this).isSneaking();
	}

	@Override
	public boolean t$isSwimmingPose() {
		return false;
	}

	@Override
	public boolean t$isSleepingPose() {
		return ((EntityLivingBase) (Object) this).isPlayerSleeping();
	}

	@Override
	public boolean t$isFlying() {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		return ((Object) this) instanceof EntityPlayer && ((EntityPlayer) (Object) this).capabilities.isFlying && self.isAirBorne || self.fallDistance > 1.5F;
	}

	@Override
	public boolean t$isVisibleToPlayer() {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		return self.isInvisible() && !self.isInvisibleToPlayer(Minecraft.getMinecraft().player);
	}

	@Override
	public boolean t$isPlayer() {
		return ((Object) this) instanceof EntityPlayer;
	}

	@Override
	public boolean t$isPreview() {
		return false;
	}

	@Override
	public UUID t$uuid() {
		return ((EntityLivingBase) (Object) this).getUniqueID();
	}

	@Override
	public Object t$unwrap() {
		return this;
	}
}