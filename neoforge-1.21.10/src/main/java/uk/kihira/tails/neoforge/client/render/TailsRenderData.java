/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.render;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import uk.kihira.tails.common.client.model.animation.AnimatorStorage;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;

public final class TailsRenderData {

	public ClientPartsData partsData = ClientPartsData.EMPTY;
	public final Map<ClientPartInfo, AnimatorStorage> animatorStorage = new IdentityHashMap<>();
	public boolean isFlying = false;
	public UUID uuid;

	public double cloakX, cloakY, cloakZ;
	public float bob, walkDist;
}