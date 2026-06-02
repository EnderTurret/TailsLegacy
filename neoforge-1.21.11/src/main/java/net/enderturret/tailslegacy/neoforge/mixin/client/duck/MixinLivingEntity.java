/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.mixin.client.duck;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.enderturret.tailslegacy.common.client.duck.TailsEntity;

@Mixin(LivingEntity.class)
@SuppressWarnings("cast")
public class MixinLivingEntity implements TailsEntity {

	@Override
	public double t$x() {
		return ((LivingEntity) (Object) this).getX();
	}

	@Override
	public double t$y() {
		return ((LivingEntity) (Object) this).getY();
	}

	@Override
	public double t$z() {
		return ((LivingEntity) (Object) this).getZ();
	}

	@Override
	public double t$xO() {
		return ((LivingEntity) (Object) this).xo;
	}

	@Override
	public double t$yO() {
		return ((LivingEntity) (Object) this).yo;
	}

	@Override
	public double t$zO() {
		return ((LivingEntity) (Object) this).zo;
	}

	@Override
	public float t$bob() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedBob(1) : 0;
	}

	@Override
	public float t$bobO() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedBob(0) : 0;
	}

	@Override
	public float t$walkDistance() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedWalkDistance(1) : 0;
	}

	@Override
	public float t$walkDistanceO() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedWalkDistance(0) : 0;
	}

	@Override
	public double t$xCloak() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedCloakX(1) : 0;
	}

	@Override
	public double t$yCloak() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedCloakY(1) : 0;
	}

	@Override
	public double t$zCloak() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedCloakZ(1) : 0;
	}

	@Override
	public double t$xCloakO() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedCloakX(0) : 0;
	}

	@Override
	public double t$yCloakO() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedCloakY(0) : 0;
	}

	@Override
	public double t$zCloakO() {
		return ((Object) this) instanceof ClientAvatarEntity c ? c.avatarState().getInterpolatedCloakZ(0) : 0;
	}

	@Override
	public float t$xRot() {
		return ((LivingEntity) (Object) this).getXRot();
	}

	@Override
	public float t$xRotO() {
		return ((LivingEntity) (Object) this).xRotO;
	}

	@Override
	public float t$yRot() {
		return ((LivingEntity) (Object) this).getYRot();
	}

	@Override
	public float t$yRotO() {
		return ((LivingEntity) (Object) this).yRotO;
	}

	@Override
	public float t$yBodyRot() {
		return ((LivingEntity) (Object) this).yBodyRot;
	}

	@Override
	public float t$yBodyRotO() {
		return ((LivingEntity) (Object) this).yBodyRotO;
	}

	@Override
	public float t$limbSwing() {
		return ((LivingEntity) (Object) this).walkAnimation.position();
	}

	@Override
	public float t$limbSwingAmount() {
		return ((LivingEntity) (Object) this).walkAnimation.speed();
	}

	@Override
	public boolean t$isPassenger() {
		return ((LivingEntity) (Object) this).getPose() == Pose.SITTING;
	}

	@Override
	public boolean t$isCrouching() {
		return ((LivingEntity) (Object) this).getPose() == Pose.CROUCHING;
	}

	@Override
	public boolean t$isSwimmingPose() {
		return ((LivingEntity) (Object) this).getPose() == Pose.SWIMMING;
	}

	@Override
	public boolean t$isSleepingPose() {
		return ((LivingEntity) (Object) this).getPose() == Pose.SLEEPING;
	}

	@Override
	public boolean t$isSpinAttackPose() {
		return ((LivingEntity) (Object) this).getPose() == Pose.SPIN_ATTACK;
	}

	@Override
	public boolean t$isElytraFlyingPose() {
		return ((LivingEntity) (Object) this).getPose() == Pose.FALL_FLYING;
	}

	@Override
	public boolean t$isFlying() {
		final LivingEntity self = (LivingEntity) (Object) this;
		return ((Object) this) instanceof Player player && player.getAbilities().flying || self.fallDistance > 1.5F;
	}

	@Override
	public boolean t$isVisibleToPlayer() {
		final LivingEntity self = (LivingEntity) (Object) this;
		return self.isInvisible() && !self.isInvisibleTo(Minecraft.getInstance().player);
	}

	@Override
	public boolean t$isDead() {
		return ((LivingEntity) (Object) this).isDeadOrDying();
	}

	@Override
	public boolean t$isAddedToWorld() {
		return ((LivingEntity) (Object) this).level() != null && ((LivingEntity) (Object) this).isAddedToLevel();
	}

	@Override
	public boolean t$inLiquid() {
		final LivingEntity self = (LivingEntity) (Object) this;
		return self.level().getBlockState(self.blockPosition()).getBlock() instanceof LiquidBlock;
	}

	@Override
	public double t$getWaterLevel(int x, double y, int z) {
		final LivingEntity self = (LivingEntity) (Object) this;
		double ret = -1;

		final MutableBlockPos pos = new MutableBlockPos(x, y, z);
		for (int i = -1; i <= 2; i++) {
			pos.set(x, y + i, z);
			final BlockState state = self.level().getBlockState(pos);
			if (state.getBlock() instanceof LiquidBlock) {
				final int level;
				if (state.hasProperty(BlockStateProperties.LEVEL)) level = state.getValue(BlockStateProperties.LEVEL);
				else if (state.hasProperty(BlockStateProperties.LEVEL_FLOWING)) level = state.getValue(BlockStateProperties.LEVEL_FLOWING);
				else level = 0;
				final double height = 0.85 - level / 10D;
				ret = i + height - y % 1;
			}
		}

		return ret;
	}

	@Override
	public boolean t$isPlayer() {
		return ((Object) this) instanceof ClientAvatarEntity;
	}

	@Override
	public UUID t$uuid() {
		return ((LivingEntity) (Object) this).getUUID();
	}

	@Override
	public Object t$unwrap() {
		return this;
	}
}