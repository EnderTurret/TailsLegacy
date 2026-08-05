package net.enderturret.tailslegacy.fabric.client;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import net.enderturret.tailslegacy.common.TailsInternal;
import net.enderturret.tailslegacy.fabric.common.TailsConfig;

@Internal
public final class TailsLegacyClient implements ClientModInitializer {

	@Internal
	public static String migratingData;

	@Override
	public void onInitializeClient() {
		ClientEventHandler.register();
		migratingData = TailsInternal.maybeMigrateTomlConfig(FabricLoader.getInstance().getConfigDir());
		TailsConfig.CLIENT_INSTANCE.load();
	}
}