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

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common.part.PartsData;

public record C2SPlayerDataMessage(PartsData partsData) {

	public static C2SPlayerDataMessage decode(FriendlyByteBuf buf) {
		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		if (TailsNetworkManager.DEBUG_NETWORK)
			Tails.LOGGER.info("[C2SPlayerDataMessage] Received {}", tailInfoJson);

		PartsData partsData = PartsData.EMPTY;

		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				partsData = Tails.SERVER_GSON.fromJson(tailInfoJson, PartsData.class);
			} catch (Exception e) {
				Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
			}

		return new C2SPlayerDataMessage(partsData);
	}

	public static void encode(C2SPlayerDataMessage msg, FriendlyByteBuf buf) {
		final String tailInfoJson = msg.partsData == null || msg.partsData.isEmpty() ? "" : Tails.PROXY.getSidedGson().toJson(msg.partsData);
		buf.writeUtf(tailInfoJson, Short.MAX_VALUE);
	}

	public static void handle(C2SPlayerDataMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.partsData != null) {
			final UUID uuid = UUIDUtil.getOrCreatePlayerUUID(ctx.get().getSender().getGameProfile());

			Tails.PROXY.getPartManager().set(uuid, message.partsData);

			// Tell other clients about the change.
			// TODO: This sends the user's part data to themself, which is an unnecessary packet (they already have this data).
			TailsNetworkManager.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CPlayerDataMessage(uuid, message.partsData));
		}

		ctx.get().setPacketHandled(true);
	}
}
