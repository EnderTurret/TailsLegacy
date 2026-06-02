/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client.duck;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;

import net.enderturret.tailslegacy.common.client.duck.TailsEntity;

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
	public boolean t$isSpinAttackPose() {
		return false;
	}

	@Override
	public boolean t$isElytraFlyingPose() {
		return ((EntityLivingBase) (Object) this).isElytraFlying();
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
	public boolean t$isDead() {
		return ((EntityLivingBase) (Object) this).isDead;
	}

	@Override
	public boolean t$isAddedToWorld() {
		return ((EntityLivingBase) (Object) this).world != null && ((EntityLivingBase) (Object) this).isAddedToWorld();
	}

	@Override
	public boolean t$inLiquid() {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		final IBlockState state = self.getEntityWorld().getBlockState(self.getPosition());
		return state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof IFluidBlock;
	}

	@Override
	public double t$getWaterLevel(int x, double y, int z) {
		final EntityLivingBase self = (EntityLivingBase) (Object) this;
		double ret = -1;

		final MutableBlockPos pos = new MutableBlockPos(x, (int) y, z);
		for (int i = -1; i <= 2; i++) {
			pos.setPos(x, (int) (y + i), z);
			final IBlockState state = self.getEntityWorld().getBlockState(pos);
			if (state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof IFluidBlock) {
				final int level;
				if (state.getProperties().containsKey(BlockLiquid.LEVEL)) level = state.getValue(BlockLiquid.LEVEL);
				else level = 0;
				final double height = 0.85 - level / 10D;
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