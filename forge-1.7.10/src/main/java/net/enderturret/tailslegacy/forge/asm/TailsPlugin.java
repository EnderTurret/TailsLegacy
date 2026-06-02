/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.asm;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("Tails Legacy")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1000)
public class TailsPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

	@Override
	public String[] getASMTransformerClass() { return null; }

	@Override
	public String getModContainerClass() { return null; }

	@Override
	public String getSetupClass() { return null; }

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() { return null; }

	@Override
	public String getMixinConfig() {
		return "mixins.tailslegacy.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedCoreMods) {
		return Collections.singletonList("duck.MixinResourceLocation");
	}
}