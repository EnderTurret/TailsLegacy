import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.ServerPartInfo;

public class OldSaveTest {

	private static final Format FORMAT_1_7 = format(7);
	private static final Format FORMAT_1_12 = format(12);
	private static final Format FORMAT_1_16 = format(16);
	private static final Format FORMAT_1_18 = format(18);
	private static final Format FORMAT_1_19 = format(19);

	private static final List<Format> FORMATS = List.of(FORMAT_1_7, FORMAT_1_12, FORMAT_1_16, FORMAT_1_18, FORMAT_1_19);

	public static void main(String[] args) {
		final Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(PartsData.class, new PartsData.Serializer())
				.registerTypeHierarchyAdapter(IPartInfo.class, ServerPartInfo.Serializer.INSTANCE)
				.create();

		final PartsData mostRecent = gson.fromJson(of(FORMAT_1_19.json()), PartsData.class);

		for (Format f : FORMATS) {
			final String testName = f.version() + "→" + FORMAT_1_19.version();
			try {
				final PartsData parsed = gson.fromJson(f.json(), PartsData.class);

				if (!mostRecent.equals(parsed)) {
					System.err.printf("Test %s failed:\n(Expected:)\n%s\n(Result:)\n%s\n", testName, mostRecent, parsed);
				} else {
					System.out.println("Test " + testName + " passed!");
				}
			} catch (Exception e) {
				System.err.println("Test " + testName + " failed:");
				e.printStackTrace();
			}
		}
	}

	private static JsonElement of(String json) {
		return JsonParser.parseString(json);
	}

	private static Format format(int major) {
		return new Format("1." + major, readFile("/partdatas/1" + major + ".json"));
	}

	private static String readFile(String path) {
		try (InputStream is = OldSaveTest.class.getResourceAsStream(path);
				InputStreamReader isr = (is == null ? null : new InputStreamReader(is));
				BufferedReader br = (isr == null ? null : new BufferedReader(isr))) {
			if (br == null)
				throw new FileNotFoundException(path);

			final StringBuilder sb = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				if (!sb.isEmpty())
					sb.append("\n");
				sb.append(line);
			}

			return sb.toString();
		} catch (IOException e) {
			throw new UncheckedIOException("Exception reading " + path + ":", e);
		}
	}

	private static record Format(String version, String json) {}
}