package uk.kihira.tails.proxy;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.model.ModelRendererWrapper;
import uk.kihira.tails.client.render.FakeEntityRenderHelper;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.client.render.PlayerRenderHelper;
import uk.kihira.tails.client.render.RenderPart;
import uk.kihira.tails.client.render.RenderingHandler;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsConfig;
import uk.kihira.tails.common.network.LibraryEntriesMessage;
import uk.kihira.tails.common.network.LibraryRequestMessage;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		registerMessages();
		registerHandlers();
		libraryManager = new LibraryManager.ClientLibraryManager();

		RenderPart.registerRenderHelper(PlayerEntity.class, new PlayerRenderHelper());
		RenderPart.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());
	}

	@Override
	public void addPartsData(UUID uuid, PartsData partsData) {
		if (hasPartsData(uuid))
			this.partsData.get(uuid).clearTextures();

		super.addPartsData(uuid, partsData);
	}

	@Override
	public void removePartsData(UUID uuid) {
		if (hasPartsData(uuid))
			partsData.get(uuid).clearTextures();
		super.removePartsData(uuid);
	}

	@Override
	public void clearAllPartsData() {
		for (PartsData partInfo : partsData.values())
			partInfo.clearTextures();
		super.clearAllPartsData();
	}

	@Override
	protected void registerMessages() {
		Tails.networkWrapper.registerMessage(0, PlayerDataMessage.class, PlayerDataMessage::toBytes, PlayerDataMessage::fromBytes, PlayerDataMessage::onMessage);
		Tails.networkWrapper.registerMessage(1, PlayerDataMapMessage.class, PlayerDataMapMessage::toBytes, PlayerDataMapMessage::fromBytes, PlayerDataMapMessage::onMessage);
		Tails.networkWrapper.registerMessage(2, LibraryEntriesMessage.class, LibraryEntriesMessage::toBytes, LibraryEntriesMessage::fromBytes, LibraryEntriesMessage::onMessage);
		Tails.networkWrapper.registerMessage(3, LibraryRequestMessage.class, LibraryRequestMessage::toBytes, LibraryRequestMessage::fromBytes, LibraryRequestMessage::onMessage);
		Tails.networkWrapper.registerMessage(4, ServerCapabilitiesMessage.class, ServerCapabilitiesMessage::toBytes, ServerCapabilitiesMessage::fromBytes, ServerCapabilitiesMessage::onMessage);
		//super.registerMessages();
	}

	@Override
	protected void registerHandlers() {
		final ClientEventHandler eventHandler = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);

		//super.registerHandlers();
	}

	@Override
	public void registerRenderers() {
		boolean legacyRenderer = TailsConfig.CLIENT_INSTANCE.forceLegacyRendering.get();
		if (legacyRenderer) Tails.logger.info("Legacy Renderer has been forced enabled");
		else if (ModList.get().isLoaded("SmartMoving")) {
			Tails.logger.info("Legacy Renderer enabled automatically for mod compatibility");
			legacyRenderer = true;
		}

		if (legacyRenderer) {
			MinecraftForge.EVENT_BUS.register(new RenderingHandler());

			final Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
			// Default
			PlayerModel model = skinMap.get("default").getEntityModel();
			model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.TAIL));
			model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.WINGS));
			model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.EARS));
			model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.MUZZLE));
			// Slim
			model = skinMap.get("slim").getEntityModel();
			model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.TAIL));
			model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.WINGS));
			model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.EARS));
			model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.MUZZLE));
		} else {
			final Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
			// Default
			PlayerRenderer renderPlayer = skinMap.get("default");
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedBody, PartsData.PartType.TAIL));
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedBody, PartsData.PartType.WINGS));
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedHead, PartsData.PartType.EARS));
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedHead, PartsData.PartType.MUZZLE));
			// Slim
			renderPlayer = skinMap.get("slim");
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedBody, PartsData.PartType.TAIL));
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedBody, PartsData.PartType.WINGS));
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedHead, PartsData.PartType.EARS));
			renderPlayer.addLayer(new LayerPart(renderPlayer, renderPlayer.getEntityModel().bipedHead, PartsData.PartType.MUZZLE));
		}
	}
}
