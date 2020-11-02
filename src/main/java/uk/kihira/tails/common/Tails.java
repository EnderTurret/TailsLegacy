/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
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
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.proxy.CommonProxy;

/**
 * Look! It's the main mod file!
 */
@Mod(Tails.MOD_ID)
public class Tails {

	/**
	 * It's <strike>a snow poff</strike> the mod id.
	 */
	public static final String MOD_ID = "tails";

	/**
	 * It's <strike>another snow poff</strike> the mod's logger.
	 */
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	/**
	 * <strike>Surprisingly, it's a snow poff.</strike> The channel used for Tails networking.
	 */
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> FMLNetworkConstants.IGNORESERVERONLY, v -> true, v -> true);

	/**
	 * A nice {@link Gson} instance for deserializing {@link PartsData}, among other things.
	 */
	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(PartsData.class, new PartsData.Serializer())
			.create();

	public static boolean libraryEnabled;
	public static boolean hasRemote;
	public static PartsData localPartsData;

	// I know this looks bad, but it's the only way to prevent class loading ClientProxy.
	// Placing ClientProxy::new in here class loads it anyway.
	public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> CommonProxy::makeClientProxy, () -> CommonProxy::new);

	public Tails() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (version,local) -> true));
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setup);
		modBus.addListener(this::loadComplete);
		modBus.addListener(this::onConfigChange);
	}

	private void setup(FMLCommonSetupEvent e) {
		PROXY.init();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> loadConfig(null));
	}

	private void loadComplete(FMLLoadCompleteEvent e) {
		PROXY.registerRenderers();
	}

	private void onConfigChange(ModConfig.ModConfigEvent event) {
		if (event.getConfig().getSpec() == TailsConfig.CLIENT_SPEC)
			loadConfig(event.getConfig());
	}

	public static void loadConfig(@Nullable ModConfig instance) {
		// Load local player info.
		try {
			// Load player data.
			final String localPlayerOutfit = TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.get();

			// Load default if none exists.
			if (localPlayerOutfit == null || localPlayerOutfit.isEmpty()) {
				localPartsData = new PartsData();

				for (PartType partType : PartType.values())
					localPartsData.setPartInfo(partType, PartInfo.none(partType));

				setLocalPartsData(localPartsData, instance);

				//Tails.LOGGER.debug("Created new parts data.");
			} else
				localPartsData = GSON.fromJson(localPlayerOutfit, PartsData.class);
		} catch (JsonSyntaxException e) {
			TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set("");
			Tails.LOGGER.error("Failed to load local player data: Invalid JSON syntax! Invalid data has been removed.", e);
		}

		libraryEnabled = TailsConfig.CLIENT_INSTANCE.enableLibrary.get();

		if (instance == null)
			instance = TailsConfig.getConfig(ModConfig.Type.CLIENT);

		if (instance != null)
			instance.save();
	}

	public static void setLocalPartsData(PartsData partsData, @Nullable ModConfig instance) {
		localPartsData = partsData;

		TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set(GSON.toJson(localPartsData));

		if (instance == null)
			instance = TailsConfig.getConfig(ModConfig.Type.CLIENT);

		if (instance != null)
			instance.save();
	}
}