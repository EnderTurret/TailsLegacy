package uk.kihira.tails.client.part;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.fml.config.ModConfig;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsConfig;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

public class LocalPartManager {

	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(PartsData.class, new PartsData.Serializer())
			.registerTypeHierarchyAdapter(IPartInfo.class, new ClientPartInfo.Serializer())
			.create();

	public static PartsData localPartsData;

	public static void reload() {
		// Load local player info.
		try {
			// Load player data.
			final String localPlayerOutfit = TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.get();

			// Load default if none exists.
			if (localPlayerOutfit == null || localPlayerOutfit.isEmpty()) {
				localPartsData = new PartsData();

				for (PartType partType : PartType.values())
					localPartsData.setPartInfo(partType, IPartInfo.empty());

				setLocalPartsData(localPartsData, TailsConfig.getConfig());
			} else
				localPartsData = GSON.fromJson(localPlayerOutfit, PartsData.class);
		} catch (Exception e) {
			TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set("");
			Tails.LOGGER.error("Failed to load local player data! Invalid data has been removed.", e);
			//TailsConfig.getConfig().save();
		}
	}

	public static void setLocalPartsData(PartsData partsData, @Nullable ModConfig instance) {
		localPartsData = partsData;

		TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set(GSON.toJson(localPartsData));

		TailsConfig.getConfig().save();
	}
}