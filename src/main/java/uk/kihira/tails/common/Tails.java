/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.proxy.CommonProxy;
import uk.kihira.tails.proxy.ClientProxy;

import java.util.Map;

@Mod(Tails.MOD_ID)
public class Tails {

    public static final String MOD_ID = "tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleChannel networkWrapper = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> FMLNetworkConstants.IGNORESERVERONLY, v -> true, v -> true);
    public static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(PartsData.class, new PartsDataDeserializer())
            .create();

    public static boolean libraryEnabled;
    public static boolean hasRemote;

    public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static PartsData localPartsData;

    public Tails() {
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	modBus.addListener(this::onPreInit);
    	modBus.addListener(this::onPostInit);
    	ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (version,local) -> true));
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TailsConfig.CLIENT_SPEC);
    }

    public void onPreInit(FMLCommonSetupEvent e) {
        Tails.proxy.init();
    }

    public void onPostInit(FMLLoadCompleteEvent e) {
        proxy.registerRenderers();
    }

    @SubscribeEvent
    public void onConfigChange(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == TailsConfig.CLIENT_SPEC) {
            loadConfig();
        }
    }

    /*@NetworkCheckHandler
    public boolean checkRemoteVersions(Map<String, String> versions, Dist side) {
        if (versions.containsKey(MOD_ID)) {
            String clientVer = Loader.instance().getReversedModObjectList().get(this).getVersion();
            if (!VersionParser.parseRange("[" + clientVer + ",)").containsVersion(new DefaultArtifactVersion(versions.get(MOD_ID)))) {
                logger.warn(String.format("Remote version not in acceptable version bounds! Local is %s, Remote (%s) is %s", clientVer, side.toString(), versions.get(MOD_ID)));
            }
            else {
                logger.debug(String.format("Remote version is in acceptable version bounds. Local is %s, Remote (%s) is %s", clientVer, side.toString(), versions.get(MOD_ID)));
                hasRemote = true;
            }
        }
        return true;
    }*/

    public static void loadConfig() {
        //Load local player info
        try {
            //Load Player Data
            String localPlayerOutfit = TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.get();

            //Load default if none exists
            if (localPlayerOutfit == null || localPlayerOutfit.isEmpty()) {
                localPartsData = new PartsData();
                for (PartsData.PartType partType : PartsData.PartType.values()) {
                    localPartsData.setPartInfo(partType, PartInfo.none(partType));
                }
                setLocalPartsData(localPartsData);
            } else {
                localPartsData = gson.fromJson(localPlayerOutfit, PartsData.class);
            }
        } catch (JsonSyntaxException e) {
            TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set("");
            Tails.logger.error("Failed to load local player data: Invalid JSON syntax! Invalid data being removed");
        }

        libraryEnabled = TailsConfig.CLIENT_INSTANCE.enableLibrary.get();

        TailsConfig.CLIENT_SPEC.save();
    }

    public static void setLocalPartsData(PartsData partsData) {
        localPartsData = partsData;

        TailsConfig.CLIENT_INSTANCE.localPlayerOutfit.set(gson.toJson(localPartsData));

        TailsConfig.CLIENT_SPEC.save();
    }
}
