/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common.part.PartsData;

public record S2CPlayerDataMessage(UUID uuid, PartsData partsData) {

	public static S2CPlayerDataMessage decode(FriendlyByteBuf buf) {
		final UUID uuid = buf.readUUID();

		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		if (TailsNetworkManager.DEBUG_NETWORK)
			Tails.LOGGER.info("[S2CPlayerDataMessage] Received {} = {}", uuid, tailInfoJson);

		PartsData partsData = PartsData.EMPTY;

		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				partsData = Tails.PROXY.getSidedGson().fromJson(tailInfoJson, PartsData.class);
			} catch (Exception e) {
				Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
			}

		return new S2CPlayerDataMessage(uuid, partsData);
	}

	public static void encode(S2CPlayerDataMessage msg, FriendlyByteBuf buf) {
		buf.writeUUID(msg.uuid);
		final String tailInfoJson = msg.partsData == null || msg.partsData.isEmpty() ? "" : Tails.SERVER_GSON.toJson(msg.partsData);
		buf.writeUtf(tailInfoJson, Short.MAX_VALUE);
	}

	public static void handle(S2CPlayerDataMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.partsData != null)
			Tails.PROXY.addPartsData(message.uuid, message.partsData);

		ctx.get().setPacketHandled(true);
	}
}
