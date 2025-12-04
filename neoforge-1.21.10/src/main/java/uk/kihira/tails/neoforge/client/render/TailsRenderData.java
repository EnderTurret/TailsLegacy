package uk.kihira.tails.neoforge.client.render;

import java.util.UUID;

import uk.kihira.tails.common.client.part.ClientPartsData;

public final class TailsRenderData {

	public ClientPartsData partsData = ClientPartsData.EMPTY;
	public boolean isFlying = false;
	public UUID uuid;

	public double cloakX, cloakY, cloakZ;
	public float bob, walkDist;
}