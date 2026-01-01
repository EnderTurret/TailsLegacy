/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.mixin.client.duck;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Pose;

import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.model.animation.AnimatorStorage;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.neoforge.client.render.RenderStates;

@Mixin(HumanoidRenderState.class)
@SuppressWarnings("cast")
public class MixinHumanoidRenderState implements TailsEntity {

	@Override
	public double t$x() {
		return ((HumanoidRenderState) (Object) this).x;
	}

	@Override
	public double t$y() {
		return ((HumanoidRenderState) (Object) this).y;
	}

	@Override
	public double t$z() {
		return ((HumanoidRenderState) (Object) this).z;
	}

	@Override
	public double t$xO() {
		return t$x();
	}

	@Override
	public double t$yO() {
		return t$y();
	}

	@Override
	public double t$zO() {
		return t$z();
	}

	@Override
	public float t$bob() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).bob;
	}

	@Override
	public float t$bobO() {
		return t$bob();
	}

	@Override
	public float t$walkDistance() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).walkDist;
	}

	@Override
	public float t$walkDistanceO() {
		return t$walkDistance();
	}

	@Override
	public double t$xCloak() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).cloakX;
	}

	@Override
	public double t$yCloak() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).cloakY;
	}

	@Override
	public double t$zCloak() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).cloakZ;
	}

	@Override
	public double t$xCloakO() {
		return t$xCloak();
	}

	@Override
	public double t$yCloakO() {
		return t$yCloak();
	}

	@Override
	public double t$zCloakO() {
		return t$zCloak();
	}

	@Override
	public float t$xRot() {
		return ((HumanoidRenderState) (Object) this).xRot;
	}

	@Override
	public float t$xRotO() {
		return t$xRot();
	}

	@Override
	public float t$yRot() {
		return ((HumanoidRenderState) (Object) this).yRot;
	}

	@Override
	public float t$yRotO() {
		return t$yRot();
	}

	@Override
	public float t$yBodyRot() {
		return ((HumanoidRenderState) (Object) this).bodyRot;
	}

	@Override
	public float t$yBodyRotO() {
		return ((HumanoidRenderState) (Object) this).bodyRot;
	}

	@Override
	public float t$limbSwing() {
		return ((HumanoidRenderState) (Object) this).walkAnimationPos;
	}

	@Override
	public float t$limbSwingAmount() {
		return ((HumanoidRenderState) (Object) this).walkAnimationSpeed;
	}

	@Override
	public boolean t$isPassenger() {
		return ((HumanoidRenderState) (Object) this).pose == Pose.SITTING;
	}

	@Override
	public boolean t$isCrouching() {
		return ((HumanoidRenderState) (Object) this).pose == Pose.CROUCHING;
	}

	@Override
	public boolean t$isSwimmingPose() {
		return ((HumanoidRenderState) (Object) this).pose == Pose.SWIMMING;
	}

	@Override
	public boolean t$isSleepingPose() {
		return ((HumanoidRenderState) (Object) this).pose == Pose.SLEEPING;
	}

	@Override
	public boolean t$isSpinAttackPose() {
		return ((HumanoidRenderState) (Object) this).pose == Pose.SPIN_ATTACK;
	}

	@Override
	public boolean t$isElytraFlyingPose() {
		return ((HumanoidRenderState) (Object) this).pose == Pose.FALL_FLYING;
	}

	@Override
	public boolean t$isFlying() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).isFlying;
	}

	@Override
	public boolean t$isVisibleToPlayer() {
		final HumanoidRenderState self = (HumanoidRenderState) (Object) this;
		return self.isInvisible && !self.isInvisibleToPlayer;
	}

	@Override
	public boolean t$inLiquid() {
		return ((HumanoidRenderState) (Object) this).isInWater;
	}

	@Override
	public double t$getWaterLevel(int x, double y, int z) {
		return ((HumanoidRenderState) (Object) this).isInWater ? 1 : -1;
	}

	@Override
	public boolean t$isPlayer() {
		return ((Object) this) instanceof AvatarRenderState;
	}

	@Override
	public AnimatorStorage t$getAnimatorStorage(ClientPartInfo part) {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).animatorStorage.get(part);
	}

	@Override
	public UUID t$uuid() {
		return ((HumanoidRenderState) (Object) this).getRenderDataOrThrow(RenderStates.RENDER_DATA).uuid;
	}

	@Override
	public Object t$unwrap() {
		return this;
	}
}