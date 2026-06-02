/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package test.platform;

import java.util.UUID;

import net.enderturret.tailslegacy.common.client.duck.FakeTailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;

public final class SimpleTailsEntity extends FakeTailsEntity {

	private final UUID uuid;

	public int pose = 0;
	public double cloakDistance = 0;
	public float bob = 0;
	public float walkDistance = 0;

	public SimpleTailsEntity(UUID uuid) {
		this.uuid = uuid;
	}

	@Override public float t$bob() { return bob; }
	@Override public float t$walkDistance() { return walkDistance; }
	@Override public double t$xCloak() { return cloakDistance; }
	@Override public double t$yCloak() { return cloakDistance; }
	@Override public double t$zCloak() { return cloakDistance; }

	@Override public boolean t$isPassenger() { return pose == 1; }
	@Override public boolean t$isSwimmingPose() { return pose == 2; }
	@Override public boolean t$isSleepingPose() { return pose == 3; }

	@Override public UUID t$uuid() { return uuid; }
}