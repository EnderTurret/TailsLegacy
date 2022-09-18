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
import net.minecraftforge.network.PacketDistributor;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

// C → S
public record PlayerDataMessage(UUID uuid, PartsData partsData) {

	public static PlayerDataMessage decode(FriendlyByteBuf buf) {
		final UUID uuid = buf.readUUID();

		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		if (Tails.DEBUG_NETWORK)
			Tails.LOGGER.info("[PlayerDataMessage] Received {} = {}", uuid, tailInfoJson);

		PartsData partsData = PartsData.EMPTY;

		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				partsData = Tails.SERVER_GSON.fromJson(tailInfoJson, PartsData.class);
			} catch (Exception e) {
				Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
			}

		return new PlayerDataMessage(uuid, partsData);
	}

	public static void encode(PlayerDataMessage msg, FriendlyByteBuf buf) {
		buf.writeUUID(msg.uuid);
		final String tailInfoJson = msg.partsData == null || msg.partsData.isEmpty() ? "" : Tails.PROXY.getSidedGson().toJson(msg.partsData);
		buf.writeUtf(tailInfoJson, Short.MAX_VALUE);
	}

	public static void handle(PlayerDataMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.partsData != null) {
			Tails.PROXY.addPartsData(message.uuid, message.partsData);
			// Tell other clients about the change.
			Tails.CHANNEL.send(PacketDistributor.ALL.noArg(), new PlayerDataMessage(message.uuid, message.partsData));
		}

		ctx.get().setPacketHandled(true);
	}
}
