package uk.kihira.tails.common2.client;

import java.util.ServiceLoader;

import uk.kihira.tails.common2.client.api.PartRendererRegistrar;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.TailsPartDefinition;
import uk.kihira.tails.common2.client.part.Part;

public interface TailsClientPlatform {

	public static TailsClientPlatform get() {
		if (TailsClientInternal.platform == null)
			TailsClientInternal.platform = ServiceLoader.load(TailsClientPlatform.class)
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("Couldn't find TailsClientPlatform implementation!"));

		return TailsClientInternal.platform;
	}

	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight);

	public boolean hasTexture(TResourceLocation id);
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints);
	public void releaseTexture(TResourceLocation id);

	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar);
}