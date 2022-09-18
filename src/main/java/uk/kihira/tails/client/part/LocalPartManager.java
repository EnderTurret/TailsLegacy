package uk.kihira.tails.client.part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;

import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsConfig;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.proxy.CommonProxy;

public final class LocalPartManager {

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

				setLocalPartsData(localPartsData);
			} else
				localPartsData = GSON.fromJson(localPlayerOutfit, PartsData.class);
		} catch (Exception e) {
			TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set("");
			Tails.LOGGER.error("Failed to load local player data! Invalid data has been removed.", e);
			//TailsConfig.getConfig().save();
		}
	}

	public static void setLocalPartsData(PartsData partsData) {
		localPartsData = partsData;

		TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set(GSON.toJson(localPartsData));

		TailsConfig.getConfig().save();
	}

	public static void syncToServer() {
		if (Minecraft.getInstance().level != null)
			TailsNetworkManager.CHANNEL.sendToServer(new PlayerDataMessage(ClientUtils.getPlayerUUID(), localPartsData));

		if (CommonProxy.sync != null)
			CommonProxy.sync.upload(ClientUtils.getPlayerUUID(), localPartsData);
	}
}