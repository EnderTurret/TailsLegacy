package uk.kihira.tails.forge.asm;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;

@LateMixin
public final class TailsLateMixins implements ILateMixinLoader {

	@Override
	public String getMixinConfig() {
		return "mixins.tails.late.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedMods) {
		if (Objects.requireNonNull(FMLLaunchHandler.side()) == Side.CLIENT && loadedMods.contains("moreplayermodels"))
			return Collections.singletonList("client.mpm.MixinModelMPM");

		return Collections.emptyList();
	}
}