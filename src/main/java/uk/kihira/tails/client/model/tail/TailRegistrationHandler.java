package uk.kihira.tails.client.model.tail;

import static uk.kihira.tails.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.Tails;

/**
 * Handles registration of tail {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 * @see RegisterPartRenderersEvent
 */
@Internal
@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TailRegistrationHandler {

	private TailRegistrationHandler() {}

	@SubscribeEvent
	static void registerPartRenderers(RegisterPartRenderersEvent e) {
		e.register(FLUFFY_TAIL, new PartRenderer(new FluffyTailModel()));
		e.register(DRAGON_TAIL, new PartRenderer(new DragonTailModel()));
		e.register(RACCOON_TAIL, new PartRenderer(new RaccoonTailModel()));
		e.register(DEVIL_TAIL, new PartRenderer(new DevilTailModel()));
		e.register(CAT_TAIL, new PartRenderer(new CatTailModel()));
		e.register(BIRD_TAIL, new PartRenderer(new BirdTailModel()));
		e.register(SHARK_TAIL, new PartRenderer(new SharkTailModel()));
		e.register(BUNNY_TAIL, new PartRenderer(new BunnyTailModel()));
		e.register(BEE_ABDOMEN, new PartRenderer(new BeeAbdomenModel()));
		e.register(SCORPION_TAIL, new PartRenderer(new ScorpionTailModel()));
	}
}