package uk.kihira.tails.common_gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.LibraryManager;

@Internal
public class GsonLibraryManager extends LibraryManager {

	private static final Type ENTRY_DATA_LIST = new TypeToken<List<LibraryEntryData>>() {}.getType();

	/**
	 * @return The {@link Gson} used for deserializing library entries.
	 */
	protected Gson getGson() {
		return Tails.SERVER_GSON;
	}

	@Override
	@Nullable
	protected List<LibraryEntryData> readEntries() {
		try (BufferedReader br = Files.newBufferedReader(createLibraryFile())) {
			return getGson().fromJson(br, ENTRY_DATA_LIST);
		} catch (Exception e) {
			Tails.LOGGER.error("Failed to load library entries!", e);
		}

		return List.of();
	}

	@Override
	protected void saveLibrary(Path to) {
		// [
		//   { ... },
		//   { ... }
		// ]
		final String json = libraryEntries.stream().map(getGson()::toJson).collect(Collectors.joining(",\n  ", "[\n  ", "\n]"));

		try (BufferedWriter bw = Files.newBufferedWriter(to, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			bw.write(json);
		} catch (Exception e) {
			Tails.LOGGER.error("Exception writing library:", e);
		}
	}
}