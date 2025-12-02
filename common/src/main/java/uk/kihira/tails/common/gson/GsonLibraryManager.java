package uk.kihira.tails.common.gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.TailsPlatform;

@Internal
public class GsonLibraryManager extends LibraryManager {

	private static final Type ENTRY_DATA_LIST = new TypeToken<List<LibraryEntryData>>() {}.getType();

	/**
	 * @return The {@link Gson} used for deserializing library entries.
	 */
	protected Gson getGson() {
		return TailsGsonHelper.SERVER_GSON;
	}

	@Override
	@Nullable
	protected List<LibraryEntryData> readEntries() {
		try (BufferedReader br = Files.newBufferedReader(createLibraryFile())) {
			return getGson().fromJson(br, ENTRY_DATA_LIST);
		} catch (Exception e) {
			TailsPlatform.get().logError("Failed to load library entries!", e);
		}

		return Collections.emptyList();
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
			TailsPlatform.get().logError("Exception writing library:", e);
		}
	}
}