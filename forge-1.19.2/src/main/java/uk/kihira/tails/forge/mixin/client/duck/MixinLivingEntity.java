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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.common.client.duck.TailsEntity;

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
		return ((Object) this) instanceof Player p ? p.bob : 0;
	}

	@Override
	public float t$bobO() {
		return ((Object) this) instanceof Player p ? p.oBob : 0;
	}

	@Override
	public float t$walkDistance() {
		return ((Object) this) instanceof Player p ? p.walkDist : 0;
	}

	@Override
	public float t$walkDistanceO() {
		return ((Object) this) instanceof Player p ? p.walkDistO : 0;
	}

	@Override
	public double t$xCloak() {
		return ((Object) this) instanceof Player p ? p.xCloak : 0;
	}

	@Override
	public double t$yCloak() {
		return ((Object) this) instanceof Player p ? p.yCloak : 0;
	}

	@Override
	public double t$zCloak() {
		return ((Object) this) instanceof Player p ? p.zCloak : 0;
	}

	@Override
	public double t$xCloakO() {
		return ((Object) this) instanceof Player p ? p.xCloakO : 0;
	}

	@Override
	public double t$yCloakO() {
		return ((Object) this) instanceof Player p ? p.yCloakO : 0;
	}

	@Override
	public double t$zCloakO() {
		return ((Object) this) instanceof Player p ? p.zCloakO : 0;
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
		return ((LivingEntity) (Object) this).animationPosition;
	}

	@Override
	public float t$limbSwingAmount() {
		return ((LivingEntity) (Object) this).animationSpeed;
	}

	@Override
	public boolean t$isPassenger() {
		return ((LivingEntity) (Object) this).getVehicle() != null;
	}

	@Override
	public boolean t$isCrouching() {
		return ((LivingEntity) (Object) this).isCrouching();
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
	public boolean t$isFlying() {
		final LivingEntity self = (LivingEntity) (Object) this;
		return ((Object) this) instanceof Player player && player.getAbilities().flying && self.hasImpulse || self.fallDistance > 1.5F;
	}

	@Override
	public boolean t$isVisibleToPlayer() {
		final LivingEntity self = (LivingEntity) (Object) this;
		return self.isInvisible() && !self.isInvisibleTo(Minecraft.getInstance().player);
	}

	@Override
	public boolean t$isPlayer() {
		return ((Object) this) instanceof Player;
	}

	@Override
	public boolean t$isPreview() {
		return false;
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