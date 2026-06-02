// Adapted from Auriafoxgirl's tail physics Figura script, licensed under the Apache 2.0 license.
// https://github.com/lua-gods/figura-libraries/blob/main/tail%20and%20ears/tail.lua
// https://github.com/lua-gods/figura-libraries/blob/main/LICENSE
package net.enderturret.tailslegacy.common.client.model.animation.impl;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.math.TailsVec3d;
import net.enderturret.tailslegacy.common.client.math.TailsVec4d;
import net.enderturret.tailslegacy.common.client.model.animation.AnimatorStorage;
import net.enderturret.tailslegacy.common.client.model.animation.ModelAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.DefaultModelAnimator.PartCopy;
import net.enderturret.tailslegacy.common.client.part.PartPath;
import net.enderturret.tailslegacy.common.client.part.Part.SubType;
import net.enderturret.tailslegacy.common.gson.TailsGsonHelper;

public final class AuriaTailPhysicsAnimator implements ModelAnimator {

	public final Config base;
	public final @Nullable Config swimming;

	public final TailsModelPart[] parts;
	public final PartCopy[] copies;

	public AuriaTailPhysicsAnimator(TailsModelPart model, JsonObject obj) {
		base = new Config(TailsGsonHelper.getAsJsonObject(obj, "config"));
		swimming = obj.has("swimmingConfig") ? new Config(TailsGsonHelper.getAsJsonObject(obj, "swimmingConfig")) : base;

		JsonArray array = TailsGsonHelper.getAsJsonArray(obj, "parts");
		parts = new TailsModelPart[array.size()];
		for (int i = 0; i < array.size(); i++)
			parts[i] = new PartPath(TailsGsonHelper.convertToString(array.get(i), "parts[" + i + "]")).traverse(model);

		array = obj.has("copies") ? TailsGsonHelper.getAsJsonArray(obj, "copies") : new JsonArray();
		copies = new PartCopy[array.size()];
		for (int i = 0; i < array.size(); i++) {
			final JsonObject o = TailsGsonHelper.convertToJsonObject(array.get(i), "copies[" + i + "]");
			copies[i] = new PartCopy(
					new PartPath(TailsGsonHelper.getAsString(o, "from")).traverse(model),
					new PartPath(TailsGsonHelper.getAsString(o, "to")).traverse(model));
		}
	}

	@Override
	public boolean isTicking() {
		return true;
	}

	@Override
	public AnimatorStorage tick(@Nullable AnimatorStorage storage, TailsEntity entity) {
		boolean created = false;
		if (!(storage instanceof Storage)) {
			storage = new Storage();
			created = true;
		}

		final Storage store = (Storage) storage;
		store.currentConfig = base;
		if (created) {
			// Try to offset the wag cycle by a number derived from the owner's ID. (See PartModelHelper#getAnimationTime for similar logic.)
			final double factor = Math.abs(entity.t$uuid().hashCode() % 100);
			store.wagTime = store.wagTime.add(store.currentConfig.idleSpeed.scale(factor));
		}

		final double bodyRot = entity.t$yBodyRot();
		TailsVec3d velocityRaw = new TailsVec3d(entity.t$x() - entity.t$xO(), entity.t$y() - entity.t$yO(), entity.t$z() - entity.t$zO());
		velocityRaw = velocityRaw.rotateAroundAxis(TailsVec3d.ONE_Y, bodyRot);
		double bodyVelocity = (bodyRot - entity.t$yBodyRotO() + 180) % 360 - 180;

		double bodyPitch = 0;
		double waterStrength = 0;
		double baseWagWalkSpeed = 1;

		if (entity.t$isSwimmingPose()) {
			bodyPitch = -90 - (entity.t$inLiquid() ? entity.t$xRot() : 0);
			waterStrength = 0.5;
			store.currentConfig = swimming;
		} else if (entity.t$isElytraFlyingPose() || entity.t$isSpinAttackPose()) {
			bodyPitch = -90 - entity.t$xRot();
			baseWagWalkSpeed = 0;
		}

		velocityRaw = velocityRaw.rotateAroundAxis(TailsVec3d.ONE_X, bodyPitch);

		// ------

		if (store.tailDelay != store.currentConfig.tailDelay || store.rot == null) {
			store.tailDelay = store.currentConfig.tailDelay;
			store.rot = new TailsVec4d[store.tailDelay];
			store.oldRot = new TailsVec4d[store.rot.length];
			Arrays.fill(store.rot, TailsVec4d.ZERO);
			Arrays.fill(store.oldRot, TailsVec4d.ZERO);
		}

		store.oldRot[0] = store.rot[0];
		for (int i = store.tailDelay - 1; i > 0; i--) {
			store.oldRot[i] = store.rot[i];
			store.rot[i] = store.rot[i - 1];
		}
		store.oldWagTime = store.wagTime;
		store.oldWagStrength = store.wagStrength;

		bodyVelocity = TailsMath.clamp(bodyVelocity * store.currentConfig.rotVelocityStrength * 0.1, -store.currentConfig.rotVelocityLimit, store.currentConfig.rotVelocityLimit);
		final double wagWalkSpeed = store.currentConfig.walkLimit == 0 ? 0 : TailsMath.clamp(velocityRaw.z() * store.currentConfig.velocityStrength.z() / store.currentConfig.walkLimit * baseWagWalkSpeed, 0, 1);
		final TailsVec3d velocity = velocityRaw.multiply(store.currentConfig.velocityStrength);

		final TailsVec3d tailPos = new TailsVec3d(entity.t$x(), entity.t$y() + store.tailY, entity.t$z());
		final double waterLevel = entity.t$getWaterLevel((int) tailPos.x(), tailPos.y(), (int) tailPos.z());
		final double inWater = TailsMath.clamp(waterLevel + 0.5, 0, 1) * store.currentConfig.waterStrength * waterStrength;

		final TailsVec4d acceleration = new TailsVec4d(
				TailsMath.clamp(velocity.y() * 2 - inWater * 4, store.currentConfig.verticalVelocityMin, store.currentConfig.verticalVelocityMax),
				bodyVelocity * Math.max(1 - Math.abs(velocityRaw.x()) * 6, 0) + TailsMath.clamp(velocity.x(), -2, 2),
				0,
				TailsMath.clamp(velocity.z() * 0.6 + Math.abs(bodyVelocity) * 0.02 + inWater * 0.25 - velocity.y() * 0.12, 0, 1) * -store.rot[0].w()
				);

		final TailsVec4d stiffRaw = new TailsVec4d(TailsMath.lerp(inWater, store.currentConfig.stiff, store.currentConfig.waterStiff));
		final TailsVec4d stiff = TailsVec4d.ONE_XYZW.add(
				stiffRaw.lerp(
						stiffRaw.add(store.currentConfig.extraMovingStiff).apply(v -> Math.min(v, 1)),
						acceleration.scale(store.currentConfig.movingStiffStrength).apply(Math::abs).apply(v -> Math.min(v, 1))
						).scale(-1));
		store.velocity = store.velocity.multiply(stiff);
		store.velocity = store.velocity.add(TailsVec4d.ZERO.add(store.rot[0].scale(-1)).scale(store.currentConfig.bounce));
		store.velocity = store.velocity.add(acceleration);
		store.rot[0] = store.rot[0].add(store.velocity);

		final TailsVec3d targetWagSpeed = store.currentConfig.idleSpeed.lerp(store.currentConfig.walkSpeed, wagWalkSpeed);
		final TailsVec3d targetWagStrength = store.currentConfig.idleStrength.lerp(store.currentConfig.walkStrength, wagWalkSpeed);
		store.wagSpeed = store.wagSpeed.lerp(targetWagSpeed, 0.15);
		store.wagStrength = store.wagStrength.lerp(targetWagStrength, 0.15);
		store.wagTime = store.wagTime.add(store.wagSpeed.scale(1 - inWater * 0.25));

		return store;
	}

	@Override
	public void setupAnim(@Nullable AnimatorStorage storage, TailsEntity entity, TailsModelPart model, SubType subType, float partialTick) {
		if (!(storage instanceof Storage)) {
			for (TailsModelPart part : parts)
				part.t$setRotationRadians(part.t$getInitialXRot(), part.t$getInitialYRot(), part.t$getInitialZRot());
			return;
		}

		final Storage store = (Storage) storage;
		if (store.rot == null || store.oldRot == null) return;

		final TailsVec3d wagTime = store.oldWagTime.lerp(store.wagTime, partialTick);
		final TailsVec3d wagStrength = store.oldWagStrength.lerp(store.wagStrength, partialTick);

		for (int i = 0; i < parts.length; i++) {
			final int key = (int) ((double) i / parts.length * store.tailDelay);
			final TailsVec4d rot = store.oldRot[key].lerp(store.rot[key], partialTick);

			double x = -rot.x();
			double y = -rot.y();
			double z = -rot.z();

			x += parts[i].t$getInitialXRot() * TailsMath.RAD_TO_DEG_D * rot.w();
			y += parts[i].t$getInitialYRot() * TailsMath.RAD_TO_DEG_D * rot.w();
			z += parts[i].t$getInitialZRot() * TailsMath.RAD_TO_DEG_D * rot.w();

			final double wagModifier = -store.currentConfig.tailOffset * i;
			final TailsVec3d wagVec = wagTime.add(wagModifier).apply(v -> TailsMath.cos((float) v)).multiply(wagStrength);

			x += wagVec.x();
			y += wagVec.y();
			z += wagVec.z();

			parts[i].t$setRotationRadians(
					x * TailsMath.DEG_TO_RAD_D,
					y * TailsMath.DEG_TO_RAD_D,
					z * TailsMath.DEG_TO_RAD_D);
		}

		for (PartCopy copy : copies)
			copy.rotate();
	}

	public static final class Storage implements AnimatorStorage {

		Config currentConfig;
		float tailY;
		int tailDelay;
		TailsVec4d[] rot;
		TailsVec4d[] oldRot;
		TailsVec4d velocity = new TailsVec4d(0, 0, 0, 0);

		/**
		 * The 'wag' speed for the current tick.
		 * This is computed from the {@link Config#idleSpeed} and {@link Config#walkSpeed} based on the player's motion.
		 */
		TailsVec3d wagSpeed = TailsVec3d.ZERO;

		/**
		 * The strength of the 'wag' animation for a given tick.
		 * This modifies the {@link #wagTime} angles to be stronger or weaker.
		 */
		TailsVec3d wagStrength = TailsVec3d.ZERO, oldWagStrength = TailsVec3d.ZERO;

		/**
		 * The 'wag' angle for a given tick, computed from adding the {@link #wagSpeed} each tick.
		 * The angle components are {@linkplain TailsMath#cos(float) cosined} so that they smoothly repeat.
		 */
		TailsVec3d wagTime = TailsVec3d.ZERO, oldWagTime = TailsVec3d.ZERO;

		@Override
		public AnimatorStorage copy() {
			final Storage ret = new Storage();

			ret.currentConfig = currentConfig;
			ret.tailY = tailY;
			ret.tailDelay = tailDelay;
			ret.rot = rot;
			ret.oldRot = oldRot;
			ret.velocity = velocity;
			ret.wagSpeed = wagSpeed;
			ret.wagStrength = wagStrength;
			ret.oldWagStrength = oldWagStrength;
			ret.wagTime = wagTime;
			ret.oldWagTime = oldWagTime;

			return ret;
		}
	}

	public static final class Config {

		public TailsVec3d velocityStrength = new TailsVec3d(1, 1, 1);

		public double rotVelocityStrength = 1;
		public double rotVelocityLimit = 10;

		public double verticalVelocityMin = -5;
		public double verticalVelocityMax = 2;

		public double bounce = 0.1;
		public double stiff = 0.18;
		public double waterStiff = 0.5;
		public double waterStrength = 0.5;

		public double extraMovingStiff = 0.15;
		public double movingStiffStrength = 20;

		/**
		 * <p>The speed of the 'wag' animation in each direction.</p>
		 * <p>Used when not moving.</p>
		 */
		public TailsVec3d idleSpeed = new TailsVec3d(0, 0, 0);

		/**
		 * <p>The strength modifier of the 'wag' animation in each direction.
		 * This is used to scale the resulting wag angle.</p>
		 * <p>Used when not moving.</p>
		 */
		public TailsVec3d idleStrength = new TailsVec3d(0, 0, 0);

		/**
		 * <p>The speed of the 'wag' animation in each direction.</p>
		 * <p>Used when moving.</p>
		 */
		public TailsVec3d walkSpeed = new TailsVec3d(0, 0.5, 0);

		/**
		 * <p>The strength modifier of the 'wag' animation in each direction.
		 * This is used to scale the resulting wag angle.</p>
		 * <p>Used when moving.</p>
		 */
		public TailsVec3d walkStrength = new TailsVec3d(0, 6, 0);

		/**
		 * Allows limiting the impact motion has on the 'wag' animation.
		 * Higher values mean that less motion is needed to reach {@link #walkSpeed} versus {@link #idleSpeed}.
		 */
		public double walkLimit = 0.31;

		/**
		 * How much further in the 'wag' cycle each tail segment should be.
		 */
		public double tailOffset = 0.5;

		/**
		 * Determines how many segments the tail has for movement to propagate to the end of the tail.
		 */
		public int tailDelay = 6;

		public Config(JsonObject cfg) {
			if (cfg.has("velocityStrength")) velocityStrength = readVec(cfg, "velocityStrength");

			if (cfg.has("rotVelocityStrength")) rotVelocityStrength = TailsGsonHelper.getAsDouble(cfg, "rotVelocityStrength");
			if (cfg.has("rotVelocityLimit")) rotVelocityLimit = TailsGsonHelper.getAsDouble(cfg, "rotVelocityLimit");

			if (cfg.has("verticalVelocityMin")) verticalVelocityMin = TailsGsonHelper.getAsDouble(cfg, "verticalVelocityMin");
			if (cfg.has("verticalVelocityMax")) verticalVelocityMax = TailsGsonHelper.getAsDouble(cfg, "verticalVelocityMax");

			if (cfg.has("bounce")) bounce = TailsGsonHelper.getAsDouble(cfg, "bounce");
			if (cfg.has("stiff")) stiff = TailsGsonHelper.getAsDouble(cfg, "stiff");
			if (cfg.has("waterStiff")) waterStiff = TailsGsonHelper.getAsDouble(cfg, "waterStiff");
			if (cfg.has("waterStrength")) waterStrength = TailsGsonHelper.getAsDouble(cfg, "waterStrength");

			if (cfg.has("extraMovingStiff")) extraMovingStiff = TailsGsonHelper.getAsDouble(cfg, "extraMovingStiff");
			if (cfg.has("movingStiffStrength")) movingStiffStrength = TailsGsonHelper.getAsDouble(cfg, "movingStiffStrength");

			if (cfg.has("idleSpeed")) idleSpeed = readVec(cfg, "idleSpeed");
			if (cfg.has("idleStrength")) idleStrength = readVec(cfg, "idleStrength");
			if (cfg.has("walkSpeed")) walkSpeed = readVec(cfg, "walkSpeed");
			if (cfg.has("walkStrength")) walkStrength = readVec(cfg, "walkStrength");
			if (cfg.has("walkLimit")) walkLimit = TailsGsonHelper.getAsDouble(cfg, "walkLimit");

			if (cfg.has("tailOffset")) tailOffset = TailsGsonHelper.getAsDouble(cfg, "tailOffset");
			if (cfg.has("tailDelay")) tailDelay = TailsGsonHelper.getAsInt(cfg, "tailDelay");
		}

		private static TailsVec3d readVec(JsonObject parent, String name) {
			final JsonArray array = TailsGsonHelper.getAsJsonArray(parent, name);
			return new TailsVec3d(
					TailsGsonHelper.convertToDouble(array.get(0), name + "[0]"),
					TailsGsonHelper.convertToDouble(array.get(1), name + "[1]"),
					TailsGsonHelper.convertToDouble(array.get(2), name + "[2]")
					);
		}
	}
}