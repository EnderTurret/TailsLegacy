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
		e.register(FOX_EARS, new FoxEarsModel());
		e.register(CAT_EARS, new CatEarsModel());
		e.register(PANDA_EARS, new PandaEarsModel());
		e.register(SMALL_CAT_EARS, new SmallCatEarsModel());
		e.register(SEA_PICKLE, new SeaPickleModel());
		e.register(ANTENNAE, new AntennaeModel());
		e.register(DEMON_HORNS, new DemonHornsModel());
		e.register(DEER_ANTLERS, new DeerAntlersModel());
		e.register(AXOLOTL_GILLS, new AxolotlGillsModel());
		e.register(STRIDER_WHISKERS, new StriderWhiskersModel());
		e.register(FLASHLIGHT, new FlashlightModel());
		e.register(FLOWER_CROWN, new FlowerCrownModel());
		e.register(UMBRELLA_HAT, new UmbrellaHatModel());
		e.register(BEANIE, new BeanieModel());
		e.register(TRAFFIC_CONE, new TrafficConeModel());
		e.register(TOP_HAT, new TopHatModel());
	}
}