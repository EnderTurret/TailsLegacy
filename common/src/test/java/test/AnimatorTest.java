package test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.enderturret.tailslegacy.common.client.duck.TailsModelPart.CubePose;
import net.enderturret.tailslegacy.common.client.model.PartModel;
import net.enderturret.tailslegacy.common.client.model.PartModelHelper;
import net.enderturret.tailslegacy.common.client.model.animation.ModelAnimator;
import net.enderturret.tailslegacy.common.client.model.animation.impl.DefaultModelAnimator;

import test.platform.SimpleTailsEntity;
import test.platform.SimpleTailsModelPart;

public final class AnimatorTest {

	private static Path animationRoot;

	public static void main(String[] args) throws Exception {
		System.setProperty("tailslegacy.testing.time", "1500");
		animationRoot = Paths.get("src/main/resources/assets/tailslegacy/tailslegacy/parts/tail");

		test("cat_tail", instantiate("CatTailModel"));
		test("devil_tail", instantiate("DevilTailModel"));
		test("dragon_tail", instantiate("DragonTailModel"));
		test("scorpion_tail", instantiate("ScorpionTailModel"));
		test("shark_tail", instantiate("SharkTailModel"));
		test("thick_tail", instantiate("ThickTailModel"));

		System.out.println("Done!");
	}

	private static void test(String jsonFile, PartModel oldModel) throws IOException {
		System.out.println("\n\n--- " + jsonFile);
		System.out.flush();

		final Map<String, SimpleTailsModelPart> parts = new LinkedHashMap<>();
		final SimpleTailsModelPart root = new SimpleTailsModelPart("", parts);

		final String json = Files.readAllLines(animationRoot.resolve(jsonFile + ".json")).stream().collect(Collectors.joining("\n"));
		final JsonObject animationJson = new JsonParser().parse(json).getAsJsonObject().getAsJsonObject("animation");
		final ModelAnimator animator = DefaultModelAnimator.parse(root, animationJson);

		root.frozen = true;

		final SimpleTailsEntity entity = new SimpleTailsEntity(new UUID(0, 0));
		for (int pose = 0; pose < 4; pose++) {
			entity.pose = pose;
			for (int cloakDistance = -10; cloakDistance <= 10; cloakDistance++) {
				entity.cloakDistance = cloakDistance;
				for (float bob = 0; bob < 2; bob += 0.1f) {
					entity.bob = bob;
					for (float walkDistance = 0; walkDistance < 2; walkDistance += 0.1f) {
						entity.walkDistance = walkDistance;
						if (!compareAnimators(parts, root, animator, oldModel, entity)) return;
					}
				}
			}
		}
	}

	private static boolean compareAnimators(Map<String, SimpleTailsModelPart> parts, SimpleTailsModelPart model, ModelAnimator animator, PartModel oldModel, SimpleTailsEntity entity) {
		for (SimpleTailsModelPart part : parts.values()) part.xRot = part.yRot = part.zRot = 0;
		animator.setupAnim(null, entity, model, null, 0);

		final Map<String, CubePose> newPoses = new LinkedHashMap<>();
		for (Map.Entry<String, SimpleTailsModelPart> entry : parts.entrySet()) newPoses.put(entry.getKey(), entry.getValue().savePose());

		for (SimpleTailsModelPart part : parts.values()) part.xRot = part.yRot = part.zRot = 0;
		oldModel.setupAnim(entity, 0, null, model);

		boolean failed = false;
		for (Map.Entry<String, SimpleTailsModelPart> entry : parts.entrySet()) {
			final SimpleTailsModelPart oldPose = entry.getValue();
			final CubePose newPose = newPoses.get(entry.getKey());

			if (!compare(oldPose.xRot, newPose.minX) || !compare(oldPose.yRot, newPose.minY) || !compare(oldPose.zRot, newPose.minZ)) {
				System.err.printf("Rotation mismatch on %s (pose %s)!\n    %f  %f  %f (original)\n    %f  %f  %f (new)\n", entry.getKey(),
						poseName(entity.pose),
						oldPose.xRot, oldPose.yRot, oldPose.zRot, newPose.minX, newPose.minY, newPose.minZ);
				System.err.flush();
				failed = true;
			}
		}

		return !failed;
	}

	private static String poseName(int pose) {
		switch (pose) {
			case 0: return "normal";
			case 1: return "sitting";
			case 2: return "swimming";
			case 3: return "sleeping";
			default: return "error";
		}
	}

	private static PartModel instantiate(String name) throws Exception {
		final Class<?> cls = Class.forName("net.enderturret.tailslegacy.common.client.model.body." + name);
		final Constructor<?> ctor = cls.getDeclaredConstructor();
		ctor.setAccessible(true);
		return (PartModel) ctor.newInstance();
	}

	private static final double EPSILON = 0.0000001;

	private static boolean compare(float a, float b) {
		return Math.abs((double) a - (double) b) < EPSILON;
	}
}