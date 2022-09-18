/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.ServerPartInfo;
import uk.kihira.tails.proxy.CommonProxy;

/**
 * Look! It's the main mod file!
 */
@Mod(Tails.MOD_ID)
public final class Tails {

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
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> "™", v -> true, v -> true);

	/**
	 * Whether to enable network debugging features, such as printing received packet data to the log.
	 */
	public static final boolean DEBUG_NETWORK = Boolean.getBoolean("tails.debugNetwork");

	// I know this looks bad, but it's the only way to prevent class loading ClientProxy.
	// Placing ClientProxy::new in here class loads it anyway.
	public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> CommonProxy::makeClientProxy, () -> CommonProxy::new);

	/**
	 * A nice {@link Gson} instance for deserializing {@link PartsData}, among other things.
	 */
	public static final Gson SERVER_GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(PartsData.class, new PartsData.Serializer())
			.registerTypeHierarchyAdapter(IPartInfo.class, new ServerPartInfo.Serializer())
			.create();

	public Tails() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "I am in fact a client-side mod.", (version,remote) -> remote));
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setup);
	}

	private void setup(FMLCommonSetupEvent e) {
		PROXY.init();
	}
}