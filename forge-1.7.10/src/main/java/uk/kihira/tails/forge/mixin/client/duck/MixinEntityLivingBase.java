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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fluids.IFluidBlock;

import uk.kihira.tails.common.TailsMath;
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
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).field_71094_bP : 0;
	}

	@Override
	public double t$yCloak() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).field_71095_bQ : 0;
	}

	@Override
	public double t$zCloak() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).field_71085_bR : 0;
	}

	@Override
	public double t$xCloakO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).field_71091_bM : 0;
	}

	@Override
	public double t$yCloakO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).field_71096_bN : 0;
	}

	@Override
	public double t$zCloakO() {
		return ((Object) this) instanceof EntityPlayer ? ((EntityPlayer) (Object) this).field_71097_bO : 0;
	}

	@Override
	public float t$xRot() {
		return ((EntityLivingBase) (Object) this).rotationPitch;
	}

	@Override
	public float t$xRotO() {
		return ((EntityLivingBase) (Object) this).prevRotationPitch;
	}

	@Override
	public float t$yRot() {
		return ((EntityLivingBase) (Object) this).rotationYaw;
	}

	@Override
	public float t$yRotO() {
		return ((EntityLivingBase) (Object) this).prevRotationYaw;
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
		return ((EntityLivingBase) (Object) this).isRiding();
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
	public boolean t$isSpinAttackPose() {
		return false;
	}

	@Override
	public boolean t$isElytraFlyingPose() {
		return false;
	}

	@Override
	public boolean t$isFlying() {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		return ((Object) this) instanceof EntityPlayer && ((EntityPlayer) (Object) this).capabilities.isFlying && self.isAirBorne || self.fallDistance > 1.5F;
	}

	@Override
	public boolean t$isVisibleToPlayer() {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		return self.isInvisible() && !self.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public boolean t$inLiquid() {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		final Block block = self.worldObj.getBlock((int) self.posX, (int) self.posY, (int) self.posZ);
		return block instanceof BlockLiquid || block instanceof IFluidBlock;
	}

	@Override
	public double t$getWaterLevel(int x, double y, int z) {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		double ret = -1;

		for (int i = -1; i <= 2; i++) {
			final Block block = self.worldObj.getBlock(x, (int) (y + i), z);
			if (block instanceof BlockLiquid || block instanceof IFluidBlock) {
				final double height;
				if (block instanceof IFluidBlock) height = (int) Math.abs(((IFluidBlock) block).getFilledPercentage(self.worldObj, x, (int) (y + i), z));
				else height = 0.85 - BlockLiquid.getLiquidHeightPercent(self.worldObj.getBlockMetadata(x, (int) (y + i), z));
				ret = i + height - y % 1;
			}
		}

		return ret;
	}

	@Override
	public boolean t$isPlayer() {
		return ((Object) this) instanceof EntityPlayer;
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