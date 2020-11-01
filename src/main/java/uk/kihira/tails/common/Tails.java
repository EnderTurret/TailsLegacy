/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import uk.kihira.tails.proxy.ClientProxy;
import uk.kihira.tails.proxy.CommonProxy;

@Mod(Tails.MOD_ID)
public class Tails {

	public static final String MOD_ID = "tails";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> FMLNetworkConstants.IGNORESERVERONLY, v -> true, v -> true);
	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(PartsData.class, new PartsDataDeserializer())
			.create();

	public static boolean libraryEnabled;
	public static boolean hasRemote;

	public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public static PartsData localPartsData;

	public Tails() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::onPreInit);
		modBus.addListener(this::onPostInit);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (version,local) -> true));
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
	}

	public void onPreInit(FMLCommonSetupEvent e) {
		Tails.PROXY.init();
	}

	public void onPostInit(FMLLoadCompleteEvent e) {
		PROXY.registerRenderers();
	}

	@SubscribeEvent
	public void onConfigChange(ModConfig.ModConfigEvent event) {
		if (event.getConfig().getSpec() == TailsConfig.CLIENT_SPEC)
			loadConfig();
	}

	public static void loadConfig() {
		// Load local player info.
		try {
			// Load player data.
			final String localPlayerOutfit = TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.get();

			// Load default if none exists.
			if (localPlayerOutfit == null || localPlayerOutfit.isEmpty()) {
				localPartsData = new PartsData();
				for (PartsData.PartType partType : PartsData.PartType.values())
					localPartsData.setPartInfo(partType, PartInfo.none(partType));
				setLocalPartsData(localPartsData);
			} else
				localPartsData = GSON.fromJson(localPlayerOutfit, PartsData.class);
		} catch (JsonSyntaxException e) {
			TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set("");
			Tails.LOGGER.error("Failed to load local player data: Invalid JSON syntax! Invalid data being removed");
		}

		libraryEnabled = TailsConfig.CLIENT_INSTANCE.enableLibrary.get();

		TailsConfig.CLIENT_SPEC.save();
	}

	public static void setLocalPartsData(PartsData partsData) {
		localPartsData = partsData;

		TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set(GSON.toJson(localPartsData));

		TailsConfig.CLIENT_SPEC.save();
	}
}
