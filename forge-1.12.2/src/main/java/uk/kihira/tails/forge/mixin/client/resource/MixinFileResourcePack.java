package uk.kihira.tails.forge.mixin.client.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.forge.common.platform.ResourceManagerExtensions;

@Mixin(FileResourcePack.class)
public abstract class MixinFileResourcePack implements ResourceManagerExtensions {

	@Override
	public Collection<ResourceLocation> tails$listResources(String prefix, Predicate<ResourceLocation> filter) {
		final Set<ResourceLocation> ret = new HashSet<>();

		try {
			final ZipFile zip = getResourcePackZipFile();

			final Enumeration<? extends ZipEntry> enumeration = zip.entries();

			while (enumeration.hasMoreElements()) {
				final ZipEntry entry = enumeration.nextElement();
				if (entry.isDirectory()) continue;

				String name = entry.getName();
				if (!name.endsWith(".mcmeta") && name.startsWith("assets/")) {
					name = name.substring("assets/".length());

					int idx = name.indexOf('/');
					final String namespace = name.substring(0, idx);
					final String path = name.substring(idx + 1);

					if (!path.startsWith(prefix)) continue;

					final ResourceLocation rl = new ResourceLocation(namespace, path);
					if (filter.test(rl))
						ret.add(rl);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	@Shadow
	private ZipFile getResourcePackZipFile() throws IOException { return null; }
}