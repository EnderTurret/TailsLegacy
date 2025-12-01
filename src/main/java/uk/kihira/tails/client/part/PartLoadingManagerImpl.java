package uk.kihira.tails.client.part;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import uk.kihira.tails.common.ResourceManagerWrapperImpl;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.part.AttachmentPoint;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.part.PartLoadingManager;
import uk.kihira.tails.common2.client.part.PartRegistry;

@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
public final class PartLoadingManagerImpl extends PartLoadingManager implements ResourceManagerReloadListener {

	public PartLoadingManagerImpl(Runnable clear, BiConsumer<List<Part>, Map<AttachmentPoint, List<TResourceLocation>>> onComplete) {
		super(clear, onComplete);
	}

	@SubscribeEvent
	static void registerReloadListeners(RegisterClientReloadListenersEvent e) {
		e.registerReloadListener((PartLoadingManagerImpl) PartRegistry.MANAGER);
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		reload(new ResourceManagerWrapperImpl(manager));
	}
}