/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.duck;

import java.util.UUID;

import uk.kihira.tails.common.TailsPlatform;

public class FakeTailsEntity implements TailsEntity {

	private final UUID uuid;

	public FakeTailsEntity(UUID uuid) {
		this.uuid = uuid;
	}

	public FakeTailsEntity() {
		this(TailsPlatform.get().randomUUID());
	}

	private static FakeTailsEntity instance;

	public static FakeTailsEntity getInstance() {
		if (instance == null) instance = new FakeTailsEntity();
		return instance;
	}

	@Override public double t$x() { return 0; }
	@Override public double t$y() { return 0; }
	@Override public double t$z() { return 0; }
	@Override public float t$bob() { return 0; }
	@Override public float t$walkDistance() { return 0; }
	@Override public double t$xCloak() { return 0; }
	@Override public double t$yCloak() { return 0; }
	@Override public double t$zCloak() { return 0; }
	@Override public float t$xRot() { return 0; }
	@Override public float t$yRot() { return 0; }
	@Override public float t$yBodyRot() { return 0; }
	@Override public float t$limbSwing() { return 0; }
	@Override public float t$limbSwingAmount() { return 0; }

	@Override public double t$xO() { return t$x(); }
	@Override public double t$yO() { return t$y(); }
	@Override public double t$zO() { return t$z(); }
	@Override public float t$bobO() { return t$bob(); }
	@Override public float t$walkDistanceO() { return t$walkDistance(); }
	@Override public double t$xCloakO() { return t$xCloak(); }
	@Override public double t$yCloakO() { return t$yCloak(); }
	@Override public double t$zCloakO() { return t$zCloak(); }
	@Override public float t$xRotO() { return t$xRot(); }
	@Override public float t$yRotO() { return t$yRot(); }
	@Override public float t$yBodyRotO() { return t$yBodyRot(); }

	@Override public boolean t$isPassenger() { return false; }
	@Override public boolean t$isCrouching() { return false; }
	@Override public boolean t$isSwimmingPose() { return false; }
	@Override public boolean t$isSleepingPose() { return false; }
	@Override public boolean t$isElytraFlyingPose() { return false; }
	@Override public boolean t$isSpinAttackPose() { return false; }
	@Override public boolean t$isFlying() { return false; }

	@Override public boolean t$isVisibleToPlayer() { return false; }

	@Override public boolean t$isPlayer() { return false; }
	@Override public boolean t$isPreview() { return true; }

	@Override public UUID t$uuid() { return uuid; }

	@Override public Object t$unwrap() { throw new UnsupportedOperationException(); }
}