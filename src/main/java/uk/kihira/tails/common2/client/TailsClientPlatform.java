package uk.kihira.tails.common2.client;

import java.util.ServiceLoader;

import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.TailsPartDefinition;

public interface TailsClientPlatform {

	public static TailsClientPlatform get() {
		if (TailsClientInternal.platform == null)
			TailsClientInternal.platform = ServiceLoader.load(TailsClientPlatform.class)
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("Couldn't find TailsClientPlatform implementation!"));

		return TailsClientInternal.platform;
	}

	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight);
}