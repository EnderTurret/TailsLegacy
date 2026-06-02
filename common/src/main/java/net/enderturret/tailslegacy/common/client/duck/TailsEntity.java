/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.duck;

import java.util.UUID;

import net.enderturret.tailslegacy.common.client.model.animation.AnimatorStorage;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;

public interface TailsEntity {

	public double t$x();
	public double t$y();
	public double t$z();
	public double t$xO();
	public double t$yO();
	public double t$zO();

	public float t$bob();
	public float t$bobO();
	public float t$walkDistance();
	public float t$walkDistanceO();

	public double t$xCloak();
	public double t$yCloak();
	public double t$zCloak();
	public double t$xCloakO();
	public double t$yCloakO();
	public double t$zCloakO();

	public float t$xRot();
	public float t$xRotO();
	public float t$yRot();
	public float t$yRotO();
	public float t$yBodyRot();
	public float t$yBodyRotO();

	public float t$limbSwing();
	public float t$limbSwingAmount();

	public boolean t$isPassenger();
	public boolean t$isCrouching();
	public boolean t$isSwimmingPose();
	public boolean t$isSleepingPose();
	public boolean t$isElytraFlyingPose();
	public boolean t$isSpinAttackPose();
	public boolean t$isFlying();

	public boolean t$isVisibleToPlayer();
	public default boolean t$isDead() { return false; }
	public default boolean t$isAddedToWorld() { return true; }

	public boolean t$inLiquid();
	public double t$getWaterLevel(int x, double y, int z);

	public boolean t$isPlayer();
	public default boolean t$isPreview() { return false; }
	public default AnimatorStorage t$getAnimatorStorage(ClientPartInfo part) { return part.getAnimatorStorage(); }
	public UUID t$uuid();
	public Object t$unwrap();
}