package uk.kihira.tails.common.client;

import java.util.ServiceLoader;
import java.util.UUID;

import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.TailsPartDefinition;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.Part;

public interface TailsClientPlatform {

	public static TailsClientPlatform get() {
		if (TailsClientInternal.platform == null)
			TailsClientInternal.platform = ServiceLoader.load(TailsClientPlatform.class)
					.iterator().next();

		return TailsClientInternal.platform;
	}

	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight);

	public boolean hasTexture(TResourceLocation id);
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints);
	public void releaseTexture(TResourceLocation id);

	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar);

	public String fetchUsername(UUID uuid);
	public UUID getLocalUUID();

	public String getConfigParts();
	public void setConfigParts(String json);
	public void syncLocalToServer(ClientPartsData partsData);

	public LibraryManager getLibraryManager();
}