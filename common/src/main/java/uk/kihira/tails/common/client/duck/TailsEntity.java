/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.duck;

import java.util.UUID;

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

	public float t$yBodyRot();
	public float t$yBodyRotO();

	public float t$limbSwing();
	public float t$limbSwingAmount();

	public boolean t$isPassenger();
	public boolean t$isCrouching();
	public boolean t$isSwimmingPose();
	public boolean t$isSleepingPose();
	public boolean t$isFlying();

	public boolean t$isVisibleToPlayer();

	public boolean t$isPlayer();
	public boolean t$isPreview();
	public UUID t$uuid();
	public Object t$unwrap();
}