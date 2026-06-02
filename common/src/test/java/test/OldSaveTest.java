/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.enderturret.tailslegacy.common.client.part.PartRegistry;
import net.enderturret.tailslegacy.common.gson.PartsDataSerializer;
import net.enderturret.tailslegacy.common.gson.ServerPartInfoSerializer;
import net.enderturret.tailslegacy.common.part.IPartInfo;
import net.enderturret.tailslegacy.common.part.PartsData;

import test.platform.SimpleResourceManager;

/**
 * Tests that ensure that old Tails data can be upgraded to newer versions without any problems.
 * @author EnderTurret
 */
public class OldSaveTest {

	private static final Format FORMAT_1_7 = format("1.7");
	private static final Format FORMAT_1_12 = format("1.12");
	private static final Format FORMAT_1_16 = format("1.16");
	private static final Format FORMAT_1_18 = format("1.18");
	private static final Format FORMAT_1_19 = format("1.19");
	private static final Format FORMAT_1_19_2 = format("1.19.2");
	private static final Format FORMAT_1_21_1 = format("1.21.1");
	private static final Format FORMAT_26_1 = format("26.1");

	private static final List<Format> FORMATS = new ArrayList<>();

	static {
		Collections.addAll(FORMATS, FORMAT_1_7, FORMAT_1_12, FORMAT_1_16, FORMAT_1_18, FORMAT_1_19, FORMAT_1_19_2, FORMAT_1_21_1, FORMAT_26_1);
	}

	public static void main(String[] args) {
		System.setProperty("tailslegacy.testing", "true");
		System.setProperty("tailslegacy.testing.suppressLog", "true");

		PartRegistry.MANAGER.reload(new SimpleResourceManager());

		final Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(PartsData.class, new PartsDataSerializer())
				.registerTypeHierarchyAdapter(IPartInfo.class, ServerPartInfoSerializer.INSTANCE)
				.setPrettyPrinting()
				.create();

		final Format current = FORMATS.get(FORMATS.size() - 1);
		final PartsData mostRecent = gson.fromJson(of(current.json), PartsData.class);

		for (Format f : FORMATS)
			test(f.version + "→" + current.version, mostRecent,
					() -> gson.fromJson(f.json, PartsData.class));

		test(current.version + "→json", current.json, () -> gson.toJson(mostRecent));
	}

	private static <T> void test(String testName, T target, Supplier<T> result) {
		try {
			final T res = result.get();
			if (!target.equals(res))
				System.err.printf("Test %s failed:\n(Expected:)\n%s\n(Result:)\n%s\n\n\n", testName, target, res);
			else
				System.out.println("Test " + testName + " passed!\n");
		} catch (Exception e) {
			System.err.println("Test " + testName + " failed:");
			e.printStackTrace();
		}
	}

	private static JsonElement of(String json) {
		return new JsonParser().parse(json);
	}

	private static Format format(String major) {
		return new Format(major, readFile("/partdatas/" + major.replace(".", "_") + ".json"));
	}

	private static String readFile(String path) {
		try (InputStream is = OldSaveTest.class.getResourceAsStream(path);
				InputStreamReader isr = is == null ? null : new InputStreamReader(is);
				BufferedReader br = isr == null ? null : new BufferedReader(isr)) {
			if (br == null)
				throw new FileNotFoundException(path);

			final StringBuilder sb = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(line);
			}

			return sb.toString();
		} catch (IOException e) {
			throw new UncheckedIOException("Exception reading " + path + ":", e);
		}
	}

	private static final class Format {

		public final String version;
		public final String json;

		public Format(String version, String json) {
			this.version = version;
			this.json = json;
		}
	}
}