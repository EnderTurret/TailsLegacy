package uk.kihira.tails.common;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class TailsConfig {

	static final ForgeConfigSpec CLIENT_SPEC;
	public static final TailsConfig CLIENT_INSTANCE;

	static {
		final Pair<TailsConfig,ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(TailsConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT_INSTANCE = pair.getLeft();
	}

	public final ConfigValue<String> localPlayerOutfit;
	public final BooleanValue enableLibrary;
	public final BooleanValue forceLegacyRendering;

	private TailsConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client");

		localPlayerOutfit = builder.comment("Local Players outfit. Delete to remove all customisation data. Do not try to edit manually.").define("localPlayerOutfit", "");
		enableLibrary = builder.comment("Whether to enable the library system for sharing tails. This mostly matters on servers.").define("enableLibrary", true);
		forceLegacyRendering = builder.comment("Forces the legacy renderer which may have better compatibility with other mods.").define("forceLegacyRendering", false);
	}
}