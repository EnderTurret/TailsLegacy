/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package test;

import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.part.PartRegistry;
import net.enderturret.tailslegacy.common.part.Parts;

import test.platform.SimpleResourceManager;

/**
 * Tests that ensure that old Tails parts are upgraded to the expected parts.
 * @author EnderTurret
 */
public class PartRemappingTest {

	public static void main(String[] args) {
		System.setProperty("tailslegacy.testing", "true");
		System.setProperty("tailslegacy.testing.suppressLog", "true");

		PartRegistry.MANAGER.reload(new SimpleResourceManager());

		final JsonElement testJson = OldSaveTest.of(OldSaveTest.readFile("/remapping_test.json"));

		for (JsonElement testElem : testJson.getAsJsonArray())
			test(Test.parse(testElem));
	}

	private static <T> void test(Test test) {
		try {
			final NewSpec upgraded = test.from.upgrade();
			if (!upgraded.equals(test.to))
				System.err.printf("Test %s failed:\nExpected: %s\nResult: %s\n\n\n", test, test.to, upgraded);
			else
				System.out.println("Test " + test + " passed!");
		} catch (Exception e) {
			System.err.println("Test " + test + " failed:");
			e.printStackTrace();
		}
	}

	private static final class Test {

		public OldSpec from;
		public NewSpec to;

		public static Test parse(JsonElement elem) {
			final JsonObject obj = elem.getAsJsonObject();
			final Test ret = new Test();
			ret.from = OldSpec.parse(obj.getAsJsonObject("from"));
			ret.to = NewSpec.parse(obj.getAsJsonObject("to"));
			return ret;
		}

		@Override
		public String toString() {
			return String.format("%s ==> %s", from, to);
		}
	}

	private static final class OldSpec {

		public String partType;
		public int partId;
		public int subId;
		public int textureId;

		public static OldSpec parse(JsonObject from) {
			final OldSpec ret = new OldSpec();
			ret.partType = from.get("partType").getAsString();
			ret.partId = from.get("partid").getAsInt();
			ret.subId = from.get("subid").getAsInt();
			ret.textureId = from.get("textureid").getAsInt();
			return ret;
		}

		public NewSpec upgrade() {
			final NewSpec ret = new NewSpec();
			final TResourceLocation part = Parts.byLegacyId(partType, partId, subId, textureId);
			ret.part = part.toString();
			ret.subType = Parts.legacySubType(part, subId);
			ret.texture = Parts.legacyTexture(part, textureId);
			return ret;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof OldSpec)) return false;
			final OldSpec o = (OldSpec) obj;
			return partType.equals(o.partType) && partId == o.partId && subId == o.subId && textureId == o.textureId;
		}

		@Override
		public int hashCode() {
			return Objects.hash(partType, partId, subId, textureId);
		}

		@Override
		public String toString() {
			return String.format("%s.%d.%d.%d", partType, partId, subId, textureId);
		}
	}

	private static final class NewSpec {

		public String part;
		public String subType;
		public String texture;

		public static NewSpec parse(JsonObject from) {
			final NewSpec ret = new NewSpec();
			ret.part = from.get("part").getAsString();
			ret.subType = from.get("subType").getAsString();
			ret.texture = from.get("texture").getAsString();
			return ret;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof NewSpec)) return false;
			final NewSpec n = (NewSpec) obj;
			return part.equals(n.part) && subType.equals(n.subType) && texture.equals(n.texture);
		}

		@Override
		public int hashCode() {
			return Objects.hash(part, subType, texture);
		}

		@Override
		public String toString() {
			return String.format("%s.%s.%s", part, subType, texture);
		}
	}
}