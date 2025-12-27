package test.platform;

import java.util.UUID;

import uk.kihira.tails.common.client.duck.TailsEntity;

public final class SimpleTailsEntity implements TailsEntity {

	private final UUID uuid;

	public int pose = 0;
	public double cloakDistance = 0;
	public float bob = 0;
	public float walkDistance = 0;

	public SimpleTailsEntity(UUID uuid) {
		this.uuid = uuid;
	}

	@Override public double t$x() { return 0; }
	@Override public double t$y() { return 0; }
	@Override public double t$z() { return 0; }
	@Override public float t$bob() { return bob; }
	@Override public float t$walkDistance() { return walkDistance; }
	@Override public double t$xCloak() { return cloakDistance; }
	@Override public double t$yCloak() { return cloakDistance; }
	@Override public double t$zCloak() { return cloakDistance; }
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
	@Override public float t$yBodyRotO() { return t$yBodyRot(); }

	@Override public boolean t$isPassenger() { return pose == 1; }
	@Override public boolean t$isCrouching() { throw new UnsupportedOperationException(); }
	@Override public boolean t$isSwimmingPose() { return pose == 2; }
	@Override public boolean t$isSleepingPose() { return pose == 3; }
	@Override public boolean t$isFlying() { throw new UnsupportedOperationException(); }

	@Override public boolean t$isVisibleToPlayer() { throw new UnsupportedOperationException(); }

	@Override public boolean t$isPlayer() { throw new UnsupportedOperationException(); }
	@Override public boolean t$isPreview() { throw new UnsupportedOperationException(); }

	@Override public UUID t$uuid() { return uuid; }

	@Override public Object t$unwrap() { throw new UnsupportedOperationException(); }
}