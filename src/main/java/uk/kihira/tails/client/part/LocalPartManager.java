/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;

import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsConfig;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common.network.C2SPlayerDataMessage;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartsData;

@Internal
public final class LocalPartManager {

	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(PartsData.class, new PartsData.Serializer())
			.registerTypeHierarchyAdapter(IPartInfo.class, new ClientPartInfo.Serializer())
			.create();

	private static PartsData localPartsData = PartsData.EMPTY;

	@Internal
	public static void reload() {
		// Load local player info.
		try {
			// Load player data.
			final String localPlayerOutfit = TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.get();

			// Load default if none exists.
			if (localPlayerOutfit == null || localPlayerOutfit.isEmpty())
				setLocalPartsData(new PartsData());
			else
				localPartsData = GSON.fromJson(localPlayerOutfit, PartsData.class);
		} catch (Exception e) {
			TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set("");
			Tails.LOGGER.error("Failed to load local player data! Invalid data has been removed.", e);
			//TailsConfig.getConfig().save();
		}
	}

	@Internal
	public static void setLocalPartsData(PartsData partsData) {
		if (partsData == null) throw new NullPointerException();

		localPartsData = partsData;

		TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set(GSON.toJson(localPartsData));

		TailsConfig.getConfig().save();
	}

	@Internal
	public static PartsData getLocalPartsData() {
		return localPartsData;
	}

	@Internal
	public static void syncToServer() {
		if (Minecraft.getInstance().level != null)
			TailsNetworkManager.CHANNEL.sendToServer(new C2SPlayerDataMessage(getLocalPartsData()));

		if (ClientPlayerPartManager.sync != null)
			ClientPlayerPartManager.sync.upload(ClientUtils.getPlayerUUID(), getLocalPartsData());
	}
}