package uk.kihira.tails.common.client.model;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsEntity;

public final class PartModelHelper {

	public static double rad(double degrees) {
		return Math.toRadians(degrees);
	}

	public static float radf(double degrees) {
		return (float) Math.toRadians(degrees);
	}

	// Returns between 0-360 in radians depending on far in the "cycle" we are.
	public static float getAnimationTime(int cycleTime, TailsEntity entity) {
		final double dCycleTime = cycleTime;
		// Note: it's tempting to refactor this to use floats (it elides the cast), but this breaks the math completely.
		// Maybe modulus only works on ints and doubles, so it chooses the int version for floats?
		return (float) ((entity.t$uuid().hashCode() + System.currentTimeMillis()) % dCycleTime / dCycleTime * 2 * Math.PI);
	}

	public static double[] getMotionAngles(TailsEntity player, float partialTick) {
		// TODO: When falling a large distance, tails tend to move wildly up and down.
		// This seems to be caused by yCloakO and yCloak being set to Y when the difference between them is greater than 10.
		// See Player.moveCloak() for details.
		final double xMotion = TailsMath.lerp(partialTick, player.t$xCloakO(), player.t$xCloak()) - TailsMath.lerp(partialTick, player.t$xO(), player.t$x());
		final double yMotion = TailsMath.lerp(partialTick, player.t$yCloakO(), player.t$yCloak()) - TailsMath.lerp(partialTick, player.t$yO(), player.t$y()); // Positive when falling, negative when climbing
		final double zMotion = TailsMath.lerp(partialTick, player.t$zCloakO(), player.t$zCloak()) - TailsMath.lerp(partialTick, player.t$zO(), player.t$z());

		final float bodyYaw = TailsMath.rotLerp(partialTick, player.t$yBodyRotO(), player.t$yBodyRot());
		// Pretty sure renderYawOffset is actually the way the body is "pointing"
		// In degrees, not bound 0-360, be warned!
		final float bodyYawRads = radf(bodyYaw);
		final double bodyYawSin = TailsMath.sin(bodyYawRads);
		final double bodyYawCos = -TailsMath.cos(bodyYawRads);

		final float xOffset = TailsMath.clamp((float) yMotion * 10F, -6F, 32F);
		float forwardMotion = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
		forwardMotion = TailsMath.clamp(forwardMotion, 0, 150);
		float sideMotion = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;
		sideMotion = TailsMath.clamp(sideMotion, -20, 20);

		if (forwardMotion < 0F) forwardMotion = 0F;

		return new double[] {
				rad(forwardMotion / 2.5 + xOffset + getTailBob(player, partialTick)),
				rad(-sideMotion / 20),
				rad(sideMotion / 2)
		};
	}

	protected static float getTailBob(TailsEntity player, float partialTick) {
		final float cameraYaw = TailsMath.lerp(partialTick, player.t$bobO(), player.t$bob());
		return TailsMath.sin(TailsMath.lerp(partialTick, player.t$walkDistanceO(), player.t$walkDistance()) * 6) * 12 * cameraYaw;
	}
}