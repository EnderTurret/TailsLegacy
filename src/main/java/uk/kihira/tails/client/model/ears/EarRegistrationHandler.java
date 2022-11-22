package uk.kihira.tails.client.model.ears;

import static uk.kihira.tails.client.part.PartRegistry.*;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.Tails;

/**
 * Handles registration of ear {@link PartRenderer PartRenderers}.
 * @author EnderTurret
 * @see RegisterPartRenderersEvent
 */
@Internal
@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EarRegistrationHandler {

	private EarRegistrationHandler() {}

	@SubscribeEvent
	static void registerPartRenderers(RegisterPartRenderersEvent e) {
		e.register(FOX_EARS, new PartRenderer(new FoxEarsModel()));
		e.register(CAT_EARS, new PartRenderer(new CatEarsModel()));
		e.register(PANDA_EARS, new PartRenderer(new PandaEarsModel()));
		e.register(SMALL_CAT_EARS, new PartRenderer(new SmallCatEarsModel()));
		e.register(SEA_PICKLE, new PartRenderer(new SeaPickleModel()));
		e.register(ANTENNAE, new PartRenderer(new AntennaeModel()));
		e.register(DEMON_HORNS, new PartRenderer(new DemonHornsModel()));
		e.register(DEER_ANTLERS, new PartRenderer(new DeerAntlersModel()));
		e.register(AXOLOTL_GILLS, new PartRenderer(new AxolotlGillsModel()));
		e.register(STRIDER_WHISKERS, new PartRenderer(new StriderWhiskersModel()));
	}
}