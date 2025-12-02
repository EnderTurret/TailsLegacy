/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.base.Strings;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common2.gson.TailsGsonHelper;
import uk.kihira.tails.common2.part.PartsData;

@Internal
public record C2SPlayerDataMessage(PartsData partsData) implements CustomPacketPayload {

	public static final Type<C2SPlayerDataMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, "sync_to_server"));

	public static final StreamCodec<ByteBuf, C2SPlayerDataMessage> STREAM_CODEC = ByteBufCodecs.stringUtf8(Short.MAX_VALUE)
			.map(C2SPlayerDataMessage::decode, C2SPlayerDataMessage::encode);

	@Override
	public Type<C2SPlayerDataMessage> type() {
		return TYPE;
	}

	private static C2SPlayerDataMessage decode(String tailInfoJson) {
		if (TailsNetworkManager.DEBUG_NETWORK)
			Tails.LOGGER.info("[C2SPlayerDataMessage] Received {}", tailInfoJson);

		PartsData partsData = PartsData.EMPTY;

		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				partsData = TailsGsonHelper.SERVER_GSON.fromJson(tailInfoJson, PartsData.class);
			} catch (Exception e) {
				Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
			}

		return new C2SPlayerDataMessage(partsData);
	}

	private static String encode(C2SPlayerDataMessage msg) {
		return msg.partsData == null || msg.partsData.isEmpty() ? "" : Tails.PROXY.getSidedGson().toJson(msg.partsData);
	}

	@Internal
	public static void handle(C2SPlayerDataMessage message, IPayloadContext context) {
		if (message.partsData != null) {
			final ServerPlayer sender = (ServerPlayer) context.player();
			final UUID uuid = sender.getUUID();

			Tails.PROXY.getPartManager().set(uuid, message.partsData);

			// Tell other clients about the change.
			// TODO: This sends the user's part data to themself, which is an unnecessary packet (they already have this data).
			PacketDistributor.sendToAllPlayers(new S2CPlayerDataMessage(uuid, message.partsData));
		}
	}
}
